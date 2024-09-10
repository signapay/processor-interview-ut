import React, { useState } from "react";
import { motion } from "framer-motion";
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
import { ChevronDown, ChevronRight } from "lucide-react"; // Add this import

interface CollectionsTableProps {
  collections: Account[];
}

const itemVariants = {
  hidden: { opacity: 0 },
  visible: { opacity: 1 },
};

export default function CollectionsTable({
  collections,
}: CollectionsTableProps) {
  const [expandedAccounts, setExpandedAccounts] = useState<string[]>([]);

  const getCollectionItems = (accounts: Account[]) => {
    return accounts
      .map((account) => ({
        name: account.name,
        cards: Object.entries(account.cards)
          .filter(([_, balance]) => balance < 0)
          .map(([card, balance]) => ({ card, balance })),
      }))
      .filter((account) => account.cards.length > 0);
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  const collectionItems = getCollectionItems(collections);

  const toggleAccountExpansion = (accountName: string) => {
    setExpandedAccounts((prev) =>
      prev.includes(accountName)
        ? prev.filter((name) => name !== accountName)
        : [...prev, accountName]
    );
  };

  return (
    <motion.div variants={itemVariants} className="flex-1">
      <Card className="h-full flex flex-col">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Collections List</CardTitle>
        </CardHeader>
        <div className="h-4"></div>
        <CardContent className="flex-grow overflow-auto">
          <div className="max-h-[400px]">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="sticky top-0 bg-background z-10">
                    Name
                  </TableHead>
                  <TableHead className="sticky top-0 bg-background z-10">
                    Card
                  </TableHead>
                  <TableHead className="sticky top-0 bg-background z-10 text-right w-1/4">
                    Balance
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {collectionItems.map((account) => (
                  <React.Fragment key={account.name}>
                    <motion.tr
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                    >
                      <TableCell>
                        <div className="flex items-center">
                          {account.cards.length > 1 && (
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
                      <TableCell className="max-w-[200px] overflow-x-auto whitespace-nowrap">
                        {account.cards[0].card}
                      </TableCell>
                      <TableCell className="text-right text-red-600">
                        {formatCurrency(account.cards[0].balance)}
                      </TableCell>
                    </motion.tr>
                    {expandedAccounts.includes(account.name) &&
                      account.cards.slice(1).map((card, index) => (
                        <motion.tr
                          key={`${account.name}-${card.card}`}
                          initial={{ opacity: 0, y: 20 }}
                          animate={{ opacity: 1, y: 0 }}
                          transition={{ delay: (index + 1) * 0.05 }}
                        >
                          <TableCell></TableCell>
                          <TableCell className="max-w-[200px] overflow-x-auto whitespace-nowrap">
                            {card.card}
                          </TableCell>
                          <TableCell className="text-right text-red-600">
                            {formatCurrency(card.balance)}
                          </TableCell>
                        </motion.tr>
                      ))}
                  </React.Fragment>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
        <div className="h-4"></div>
      </Card>
    </motion.div>
  );
}
