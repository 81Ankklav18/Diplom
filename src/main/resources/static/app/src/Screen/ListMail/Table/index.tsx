import React, { useCallback } from "react";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Checkbox from "@material-ui/core/Checkbox";
import { MailListItem } from "../../../Service/types";
import { EnhancedTableToolbar } from "./TableToolbar";
import EnhancedTableHead from "./TableHead";
import { IconButton } from "@material-ui/core";
import EditIcon from "@material-ui/icons/Edit";

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
  selected: number[];
  toggleSelectAll: (isAll: boolean) => void;
  selectItem: (id: number) => void;
  editItem: (id: number) => void;
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
          <Table className={classes.table} size="small">
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
                    <TableCell>{row.class}</TableCell>
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
