import React, { FC, useState, useCallback } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Tabs,
  Tab,
} from "@material-ui/core";
import { ClassificationResult } from "../../../../Service/types";
import SemiLatticeView from "./SemiLatticeView";
import MetricsView from "./MetricsView";

interface Props {
  isOpen: boolean;
  content: ClassificationResult | null;
  handleClose: () => void;
}

const ClassificationResultDialog: FC<Props> = ({
  content,
  handleClose,
  isOpen,
}) => {
  const [currentTab, setCurrentTab] = useState(0);
  const handleChange = useCallback((_, newValue: number) => {
    setCurrentTab(newValue);
  }, []);
  if (content === null) return null;
  return (
    <Dialog
      open={isOpen}
      onClose={handleClose}
      scroll="paper"
      aria-labelledby="scroll-dialog-title"
      aria-describedby="scroll-dialog-description"
    >
      <DialogTitle id="scroll-dialog-title">Результат анализа</DialogTitle>
      <DialogContent dividers>
        <Tabs
          value={currentTab}
          onChange={handleChange}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
        >
          <Tab label="Метрики" />
          <Tab label="Решётка" />
        </Tabs>
        {currentTab === 0 ? (
          <MetricsView content={content} />
        ) : (
          <SemiLatticeView items={content.semiLatticeViewDTO} />
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Закрыть
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ClassificationResultDialog;
