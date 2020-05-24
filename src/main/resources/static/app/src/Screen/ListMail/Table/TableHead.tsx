import React from "react";
import { TableCell, TableHead, TableRow, Checkbox } from "@material-ui/core";
import { MailListItem } from "../../../Service/types";

interface HeadCell {
  disablePadding: boolean;
  id: keyof MailListItem;
  label: string;
  numeric: boolean;
}
const headCells: HeadCell[] = [
  { id: "id", numeric: false, disablePadding: true, label: "ID" },
  { id: "subject", numeric: false, disablePadding: false, label: "Тема" },
  { id: "snippet", numeric: false, disablePadding: false, label: "Фрагмент" },
  { id: "date", numeric: false, disablePadding: false, label: "Дата" },
  { id: "label", numeric: false, disablePadding: false, label: "Класс" },
];

interface EnhancedTableProps {
  numSelected: number;
  onSelectAllClick: (event: React.ChangeEvent<HTMLInputElement>) => void;
  rowCount: number;
}

export default function EnhancedTableHead(props: EnhancedTableProps) {
  const { onSelectAllClick, numSelected, rowCount } = props;
  return (
    <TableHead>
      <TableRow>
        <TableCell padding="checkbox">
          <Checkbox
            indeterminate={numSelected > 0 && numSelected < rowCount}
            checked={rowCount > 0 && numSelected === rowCount}
            onChange={onSelectAllClick}
          />
        </TableCell>
        {headCells.map((headCell) => (
          <TableCell
            key={headCell.id}
            align={headCell.numeric ? "right" : "left"}
            padding={headCell.disablePadding ? "none" : "default"}
          >
            {headCell.label}
          </TableCell>
        ))}
        <TableCell />
      </TableRow>
    </TableHead>
  );
}
