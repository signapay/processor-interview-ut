import { Account } from "@/types/account";
import { BadTransaction } from "@/types/bad-transactions";

interface CacheData {
  accounts: Account[];
  collections: Account[];
  badTransactions: BadTransaction[];
}

class LocalStorageCache {
  private static instance: LocalStorageCache;
  private readonly STORAGE_KEY = "cardCollectionData";

  private constructor() {}

  public static getInstance(): LocalStorageCache {
    if (!LocalStorageCache.instance) {
      LocalStorageCache.instance = new LocalStorageCache();
    }
    return LocalStorageCache.instance;
  }

  public setData(data: Partial<CacheData>): void {
    const currentData = this.getData();
    const newData = { ...currentData, ...data };
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(newData));
  }

  public getData(): CacheData {
    const data = localStorage.getItem(this.STORAGE_KEY);
    return data
      ? JSON.parse(data)
      : {
          accounts: [],
          collections: [],
          badTransactions: [],
        };
  }

  public clearData(): void {
    localStorage.removeItem(this.STORAGE_KEY);
  }
}

export const cache = LocalStorageCache.getInstance();
