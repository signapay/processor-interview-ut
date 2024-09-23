from django.urls import path
from .views import FileUploadView, ChartOfAccountsView, CollectionsView, BadTransactionsView, ClearDataView


urlpatterns = [
    path('upload/', FileUploadView.as_view(), name='file-upload'),
    path('report/accounts/', ChartOfAccountsView.as_view(), name='chart-of-accounts'),
    path('report/collections/', CollectionsView.as_view(), name='collections-report'),
    path('report/bad-transactions/', BadTransactionsView.as_view(), name='bad-transactions'),
    path('clear-data/', ClearDataView.as_view(), name='clear-data'),
    path('bad-transactions/', BadTransactionsView.as_view(), name='bad-transactions'),


]
