"use client";

import { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";
import { useToast } from "@/hooks/use-toast";
import { cache } from "@/utils/cache";
import { Account } from "@/types/account";
import { BadTransaction } from "@/types/bad-transactions";
import FileUploadSection from "@/components/core/FileUploadSection";
import AccountsTable from "@/components/core/AccountsTable";
import CollectionsTable from "@/components/core/CollectionsTable";
import BadTransactionsTable from "@/components/core/BadTransactionsTable";

export default function Home() {
  const [file, setFile] = useState<File | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [collections, setCollections] = useState<Account[]>([]);
  const [badTransactions, setBadTransactions] = useState<BadTransaction[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    loadCachedData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadCachedData = () => {
    const cachedData = cache.getData();
    if (
      cachedData.accounts.length > 0 ||
      cachedData.collections.length > 0 ||
      cachedData.badTransactions.length > 0
    ) {
      toast({
        title: "Loading Data",
        description: "Retrieving data from local storage...",
      });

      setAccounts(cachedData.accounts);
      setCollections(cachedData.collections);
      setBadTransactions(cachedData.badTransactions);

      setTimeout(() => {
        toast({
          title: "Data Loaded",
          description: "Data successfully retrieved from local storage.",
          variant: "success",
        });
      }, 1000); // Show success toast after a short delay
    }
  };

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setFile(event.target.files[0]);
    }
  };

  const handleSubmit = async () => {
    if (!file) {
      toast({
        title: "Error",
        description: "Please select a file to upload.",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("/api/upload", {
        method: "POST",
        body: formData,
        cache: "no-store",
      });

      if (!response.ok) {
        throw new Error("File upload failed");
      }

      await fetchData();
      toast({
        title: "Success",
        description: "File processed successfully.",
        variant: "success",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to process file. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = async () => {
    setIsLoading(true);
    try {
      const response = await fetch("/api/reset", {
        method: "POST",
        cache: "no-store",
      });
      if (!response.ok) {
        throw new Error("Reset failed");
      }
      setAccounts([]);
      setCollections([]);
      setBadTransactions([]);
      cache.clearData();
      toast({
        title: "Success",
        description: "System reset successfully.",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to reset system. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const fetchData = async () => {
    try {
      const [accountsRes, collectionsRes, badTransactionsRes] =
        await Promise.all([
          fetch("/api/accounts", { cache: "no-store" }),
          fetch("/api/collections", { cache: "no-store" }),
          fetch("/api/bad-transactions", { cache: "no-store" }),
        ]);

      if (!accountsRes.ok || !collectionsRes.ok || !badTransactionsRes.ok) {
        throw new Error("Failed to fetch data");
      }

      const [accountsData, collectionsData, badTransactionsData] =
        await Promise.all([
          accountsRes.json(),
          collectionsRes.json(),
          badTransactionsRes.json(),
        ]);

      console.log(accountsData);
      console.log(collectionsData);
      console.log(badTransactionsData);

      const convertToArray = (data: Record<string, Record<string, number>>) =>
        Object.entries(data).map(([name, cards]) => ({
          name,
          cards,
        }));

      const accountsArray = convertToArray(accountsData);
      const collectionsArray = convertToArray(collectionsData);

      setAccounts(accountsArray);
      setCollections(collectionsArray);
      setBadTransactions(badTransactionsData);

      cache.setData({
        accounts: accountsArray,
        collections: collectionsArray,
        badTransactions: badTransactionsData,
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to fetch data. Please try again.",
        variant: "destructive",
      });
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
    },
  };

  return (
    <motion.div
      className="container mx-auto p-4"
      initial="hidden"
      animate="visible"
      variants={containerVariants}
    >
      <motion.h1
        className="text-3xl font-bold mb-6 pt-8"
        variants={itemVariants}
      >
        Transaction Processor
      </motion.h1>

      <FileUploadSection
        file={file}
        isLoading={isLoading}
        fileInputRef={fileInputRef}
        handleFileUpload={handleFileUpload}
        handleSubmit={handleSubmit}
        handleReset={handleReset}
        itemVariants={itemVariants}
      />

      <motion.div
        className="flex flex-col md:flex-row gap-6 mb-6 lg:h-[400px]"
        variants={containerVariants}
      >
        <AccountsTable accounts={accounts} itemVariants={itemVariants} />
        <CollectionsTable collections={collections} />
      </motion.div>

      <BadTransactionsTable
        badTransactions={badTransactions}
        itemVariants={itemVariants}
      />
    </motion.div>
  );
}
