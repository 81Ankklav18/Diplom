import React, { useCallback } from "react";
import {
  createStyles,
  makeStyles,
  Theme,
  IconButton,
  Checkbox,
  Paper,
  TableRow,
  TableContainer,
  TableCell,
  TableBody,
  Table,
} from "@material-ui/core";
import EditIcon from "@material-ui/icons/Edit";
import { MailListItem, Id } from "../../../Service/types";
import { EnhancedTableToolbar } from "./TableToolbar";
import EnhancedTableHead from "./TableHead";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      width: "100%",
    },
    paper: {
      width: "100%",
      marginBottom: theme.spacing(2),
    },
    table: {
      minWidth: 750,
    },
    visuallyHidden: {
      border: 0,
      clip: "rect(0 0 0 0)",
      height: 1,
      margin: -1,
      overflow: "hidden",
      padding: 0,
      position: "absolute",
      top: 20,
      width: 1,
    },
  })
);

type Props = {
  rows: MailListItem[];
  selected: Id[];
  toggleSelectAll: (isAll: boolean) => void;
  selectItem: (id: Id) => void;
  editItem: (id: Id) => void;
  removeSelected: () => void;
};

export default function EnhancedTable({
  rows,
  selected,
  toggleSelectAll,
  selectItem,
  editItem,
  removeSelected,
}: Props) {
  const classes = useStyles();
  const handleSelectAllClick = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      toggleSelectAll(event.target.checked);
    },
    [toggleSelectAll]
  );
  return (
    <div className={classes.root}>
      <Paper className={classes.paper}>
        <EnhancedTableToolbar
          numSelected={selected.length}
          remove={removeSelected}
        />
        <TableContainer>
          <Table stickyHeader className={classes.table} size="small">
            <EnhancedTableHead
              numSelected={selected.length}
              onSelectAllClick={handleSelectAllClick}
              rowCount={rows.length}
            />
            <TableBody>
              {rows.map((row, index) => {
                const isItemSelected = selected.indexOf(row.id) !== -1;
                const labelId = `enhanced-table-checkbox-${index}`;
                return (
                  <TableRow
                    hover
                    role="checkbox"
                    aria-checked={isItemSelected}
                    tabIndex={-1}
                    key={row.id}
                    selected={isItemSelected}
                  >
                    <TableCell padding="checkbox">
                      <Checkbox
                        checked={isItemSelected}
                        onClick={() => selectItem(row.id)}
                      />
                    </TableCell>
                    <TableCell
                      component="th"
                      id={labelId}
                      scope="row"
                      padding="none"
                    >
                      {row.id}
                    </TableCell>
                    <TableCell>{row.subject}</TableCell>
                    <TableCell>{row.snippet}</TableCell>
                    <TableCell>{row.date}</TableCell>
                    <TableCell>{row.label}</TableCell>
                    <TableCell padding="checkbox">
                      <IconButton onClick={() => editItem(row.id)}>
                        <EditIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </div>
  );
}
