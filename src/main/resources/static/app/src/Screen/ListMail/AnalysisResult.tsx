import React, { FC } from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";

interface Props {
  content: string | null;
  handleClose: () => void;
}

const AnalysisResult: FC<Props> = ({ content, handleClose }) => (
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

export default AnalysisResult;
