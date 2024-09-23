from django.db import models

class Transaction(models.Model):
    ACCOUNT_CHOICES = [
        ('Credit', 'Credit'),
        ('Debit', 'Debit'),
        ('Transfer', 'Transfer'),
    ]

    account_name = models.CharField(max_length=100)
    card_number = models.CharField(max_length=16)
    transaction_amount = models.DecimalField(max_digits=10, decimal_places=2)
    transaction_type = models.CharField(max_length=10, choices=ACCOUNT_CHOICES)
    description = models.TextField(null=True, blank=True)
    target_card_number = models.CharField(max_length=16, null=True, blank=True)
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.account_name} - {self.transaction_type} - {self.transaction_amount}"

class BadTransaction(models.Model):
    record = models.TextField()  # Store the entire record as a string
    error_message = models.TextField()  # Store the error message
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Bad Record at {self.timestamp}"