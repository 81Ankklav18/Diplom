import React, { FC } from "react";
import {
  DialogContentText,
  Typography,
  ExpansionPanelSummary,
  ExpansionPanel,
  ExpansionPanelDetails,
  makeStyles,
  createStyles,
  Theme,
} from "@material-ui/core";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import { SemiLattice } from "../../../../Service/types";

interface Props {
  items: SemiLattice[];
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      minWidth: "400px",
      minHeight: "300px",
    },
    heading: {
      fontSize: theme.typography.pxToRem(15),
      flexBasis: "33.33%",
      flexShrink: 0,
    },
    secondaryHeading: {
      fontSize: theme.typography.pxToRem(15),
      color: theme.palette.text.secondary,
    },
  })
);

const SemiLatticeView: FC<Props> = ({ items }) => {
  const classes = useStyles();
  return (
    <DialogContentText
      id="scroll-dialog-description"
      tabIndex={-1}
      className={classes.root}
    >
      {items.map((item) => (
        <ExpansionPanel TransitionProps={{ unmountOnExit: true }}>
          <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />}>
            <Typography className={classes.heading}>{item.title}</Typography>
            {item.ids.length !== 0 && (
              <Typography className={classes.secondaryHeading}>
                Ids: {item.ids.join(", ")}
              </Typography>
            )}
          </ExpansionPanelSummary>
          <ExpansionPanelDetails>
            <pre>{item.tree}</pre>
          </ExpansionPanelDetails>
        </ExpansionPanel>
      ))}
    </DialogContentText>
  );
};

export default SemiLatticeView;
