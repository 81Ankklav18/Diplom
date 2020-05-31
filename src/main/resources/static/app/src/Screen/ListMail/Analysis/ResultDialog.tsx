import React, { FC } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@material-ui/core";

interface Props {
  content: string | null;
  handleClose: () => void;
}

const ResultDialog: FC<Props> = ({ content, handleClose }) => (
  <Dialog
    open={content !== null}
    onClose={handleClose}
    scroll="paper"
    aria-labelledby="scroll-dialog-title"
    aria-describedby="scroll-dialog-description"
  >
    <DialogTitle id="scroll-dialog-title">Результат анализа</DialogTitle>
    <DialogContent dividers>
      <DialogContentText id="scroll-dialog-description" tabIndex={-1}>
        {content}
      </DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={handleClose} color="primary">
        Закрыть
      </Button>
    </DialogActions>
  </Dialog>
);

export default ResultDialog;
