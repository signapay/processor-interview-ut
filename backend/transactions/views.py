from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
import csv
from io import StringIO
from .models import Transaction, BadTransaction
from .serializers import TransactionSerializer
from django.db.models import Sum
from django.http import JsonResponse


class FileUploadView(APIView):
    def post(self, request, format=None):
        file_obj = request.FILES.get('file')

        if not file_obj.name.endswith('.csv'):
            return Response({"error": "Please upload a CSV file."}, status=status.HTTP_400_BAD_REQUEST)

        # Read and parse the CSV file
        file_data = file_obj.read().decode('utf-8')
        csv_reader = csv.reader(StringIO(file_data))

        transactions = []
        invalid_records = []  # List to track invalid records
        
        for row in csv_reader:
            print(row)
            if len(row) < 5:
                invalid_records.append({"record": row, "error": "Insufficient columns"})
                BadTransaction.objects.create(
                    record=str(row),
                    error_message="Insufficient columns"
                )  # Store in BadTransaction table
                continue

            account_name = row[0]
            card_number = row[1]
            transaction_amount = row[2]
            transaction_type = row[3]
            description = row[4]
            target_card_number = None

            if transaction_type == 'Transfer' and len(row) == 6:
                target_card_number = row[5]

            transaction_data = {
                "account_name": account_name,
                "card_number": card_number,
                "transaction_amount": transaction_amount,
                "transaction_type": transaction_type,
                "description": description,
                "target_card_number": target_card_number,
            }

            # Validate and save the transaction
            serializer = TransactionSerializer(data=transaction_data)
            if serializer.is_valid():
                serializer.save()
                transactions.append(serializer.data)
            else:
                # Track invalid records along with validation errors
                invalid_records.append({
                    "record": row,
                    "errors": serializer.errors
                })
                BadTransaction.objects.create(
                    record=str(row),
                    error_message=str(serializer.errors)
                )

        # Return the valid transactions and invalid records
        message="File uploaded successfully."
        if invalid_records:
            message= "File uploaded successfully with some invalid records."
        return Response({
            "message": message,
            # "valid_transactions": transactions,
            "invalid_records": invalid_records  # Return invalid records to the user
        }, status=status.HTTP_201_CREATED)


class ChartOfAccountsView(APIView):
    def get(self, request):
        # Group by account name and card number, and sum the transaction amounts
        Accounts = Transaction.objects.values('account_name', 'card_number') \
                                      .annotate(balance=Sum('transaction_amount'))

        return Response(Accounts)

class CollectionsView(APIView):
    def get(self, request):
        # List of accounts with negative balance
        collections = Transaction.objects.values('account_name', 'card_number') \
                                         .annotate(balance=Sum('transaction_amount')) \
                                         .filter(balance__lt=0)

        return Response(collections)



class ClearDataView(APIView):
    def post(self, request):
        Transaction.objects.all().delete()
        BadTransaction.objects.all().delete()
        return JsonResponse({"message": "All data cleared successfully."}, status=200)

class BadTransactionsView(APIView):
    def get(self, request):
        bad_transactions = BadTransaction.objects.all().values('record', 'error_message', 'timestamp')
        return Response(bad_transactions, status=status.HTTP_200_OK)