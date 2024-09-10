import { Button } from "@/components/ui/button";
import { motion } from "framer-motion";

interface FileUploadSectionProps {
  file: File | null;
  isLoading: boolean;
  fileInputRef: React.RefObject<HTMLInputElement>;
  handleFileUpload: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSubmit: () => void;
  handleReset: () => void;
  itemVariants: any;
}

export default function FileUploadSection({
  file,
  isLoading,
  fileInputRef,
  handleFileUpload,
  handleSubmit,
  handleReset,
  itemVariants,
}: FileUploadSectionProps) {
  const handleImportClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <motion.div variants={itemVariants}>
      <div className="flex space-x-2 mb-2">
        <input
          type="file"
          accept=".csv"
          onChange={handleFileUpload}
          ref={fileInputRef}
          className="hidden"
        />
        <Button onClick={handleImportClick} variant="outline">
          Import File
        </Button>
        <Button onClick={handleSubmit} disabled={isLoading}>
          {isLoading ? "Processing..." : "Process File"}
        </Button>
        <Button
          onClick={handleReset}
          variant="destructive"
          disabled={isLoading}
        >
          Reset System
        </Button>
      </div>
      {file && (
        <p className="mb-4 ml-3 text-sm text-gray-600">
          Selected file: {file.name}
        </p>
      )}
    </motion.div>
  );
}
