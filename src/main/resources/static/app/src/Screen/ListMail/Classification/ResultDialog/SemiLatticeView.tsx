import React, { FC } from "react";
import {
  DialogContentText,
  Typography,
  ExpansionPanelSummary,
  ExpansionPanel,
  ExpansionPanelDetails,
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import { SemiLattice } from "../../../../Service/types";

interface Props {
  items: SemiLattice[];
}

const SemiLatticeView: FC<Props> = ({ items }) => (
  <DialogContentText id="scroll-dialog-description" tabIndex={-1}>
    {items.map((item) => (
      <ExpansionPanel>
        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
          <Typography>{item.title}</Typography>
        </ExpansionPanelSummary>
        <ExpansionPanelDetails>
          <Typography>Ids: {item.ids.join(", ")}</Typography>
          <Typography>{item.tree}</Typography>
        </ExpansionPanelDetails>
      </ExpansionPanel>
    ))}
  </DialogContentText>
);

export default SemiLatticeView;
