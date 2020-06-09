import React, { FC } from "react";
import { DialogContentText, Typography } from "@material-ui/core";
import { ClassificationResult } from "../../../../Service/types";

interface Props {
  content: ClassificationResult;
}

const MetricsView: FC<Props> = ({ content }) => (
  <DialogContentText id="scroll-dialog-description" tabIndex={-1}>
    <Typography>F1: {content.f1}</Typography>
    <Typography>Precision: {content.precision}</Typography>
    <Typography>Recall: {content.recall}</Typography>
  </DialogContentText>
);

export default MetricsView;
