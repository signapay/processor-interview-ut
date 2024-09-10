import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Account } from "@/types/account";
import { ChevronDown, ChevronRight } from "lucide-react";

interface AccountsTableProps {
  accounts: Account[];
  itemVariants: any;
}

export default function AccountsTable({
  accounts,
  itemVariants,
}: AccountsTableProps) {
  const [expandedAccounts, setExpandedAccounts] = useState<string[]>([]);
  const [visibleAccounts, setVisibleAccounts] = useState<Account[]>([]);

  const calculateTotalBalance = (cards: Record<string, number>) => {
    return Object.values(cards).reduce((sum, balance) => sum + balance, 0);
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  const toggleAccountExpansion = (accountName: string) => {
    setExpandedAccounts((prev) =>
      prev.includes(accountName)
        ? prev.filter((name) => name !== accountName)
        : [...prev, accountName]
    );
  };

  useEffect(() => {
    setVisibleAccounts(accounts);
  }, [accounts]);

  return (
    <motion.div variants={itemVariants} className="flex-1">
      <Card className="h-full flex flex-col">
        <CardHeader>
          <CardTitle>Chart of Accounts</CardTitle>
        </CardHeader>
        <div className="h-4"></div>
        <CardContent className="flex-grow overflow-auto">
          <div className="max-h-[400px]">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="sticky top-0 bg-background z-10 w-1/3">
                    Name
                  </TableHead>
                  <TableHead className="sticky top-0 bg-background z-10">
                    Cards
                  </TableHead>
                  <TableHead className="sticky top-0 bg-background z-10 text-right w-1/4">
                    Balance
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <AnimatePresence>
                  {visibleAccounts.map((account) => (
                    <React.Fragment key={account.name}>
                      <motion.tr
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.2 }}
                      >
                        <TableCell>
                          <div className="flex items-center">
                            {Object.keys(account.cards).length > 1 && (
                              <button
                                onClick={() =>
                                  toggleAccountExpansion(account.name)
                                }
                                className="mr-2"
                              >
                                {expandedAccounts.includes(account.name) ? (
                                  <ChevronDown size={16} />
                                ) : (
                                  <ChevronRight size={16} />
                                )}
                              </button>
                            )}
                            {account.name}
                          </div>
                        </TableCell>
                        <TableCell>
                          {Object.keys(account.cards).length} card(s)
                        </TableCell>
                        <TableCell className="text-right">
                          {formatCurrency(calculateTotalBalance(account.cards))}
                        </TableCell>
                      </motion.tr>
                      <AnimatePresence>
                        {expandedAccounts.includes(account.name) &&
                          Object.entries(account.cards).map(
                            ([cardName, balance], cardIndex) => (
                              <motion.tr
                                key={`${account.name}-${cardName}`}
                                initial={{ opacity: 0, height: 0 }}
                                animate={{ opacity: 1, height: "auto" }}
                                exit={{ opacity: 0, height: 0 }}
                                transition={{ duration: 0.2 }}
                              >
                                <TableCell></TableCell>
                                <TableCell>{cardName}</TableCell>
                                <TableCell className="text-right">
                                  {formatCurrency(balance)}
                                </TableCell>
                              </motion.tr>
                            )
                          )}
                      </AnimatePresence>
                    </React.Fragment>
                  ))}
                </AnimatePresence>
              </TableBody>
            </Table>
          </div>
        </CardContent>
        <div className="h-4"></div>
      </Card>
    </motion.div>
  );
}
