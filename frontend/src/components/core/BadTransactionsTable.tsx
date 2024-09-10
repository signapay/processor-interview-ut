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
import { BadTransaction } from "@/types/bad-transactions";

interface BadTransactionsTableProps {
  badTransactions: BadTransaction[];
  itemVariants: any;
}

export default function BadTransactionsTable({
  badTransactions,
  itemVariants,
}: BadTransactionsTableProps) {
  return (
    <motion.div variants={itemVariants} className="mt-4">
      <Card className="flex flex-col h-[300px]">
        {" "}
        <CardHeader>
          <CardTitle>Bad Transactions</CardTitle>
        </CardHeader>
        <CardContent className="flex-grow overflow-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="sticky top-0 bg-background z-10">
                  Line
                </TableHead>
                <TableHead className="sticky top-0 bg-background z-10">
                  Content
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {badTransactions.map((transaction, _) => (
                <TableRow key={transaction.line}>
                  <TableCell>{transaction.line}</TableCell>
                  <TableCell>{transaction.content}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </motion.div>
  );
}
