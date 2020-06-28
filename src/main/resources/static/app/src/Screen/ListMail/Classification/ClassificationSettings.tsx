import React, { FC, useState, useCallback } from "react";
import {
  DialogActions,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  Typography,
} from "@material-ui/core";
import { algorithms } from "../../../Service/ClassificationAnalysis";
import SliderInput from "../SliderInput";
import SwitchInput from "../SwitchInput";
import AlgorithmInput from "../AlgorithmsInput";

interface Props {
  onSelect: (
    algorithmCode: string,
    trainPercent: number,
    hmitSearch: boolean,
    handleRoot: boolean
  ) => void;
  handleClose: () => void;
  isOpen: boolean;
}

const ClassificationSettings: FC<Props> = ({
  handleClose,
  onSelect,
  isOpen,
}) => {
  const [algo, setAlgo] = useState(algorithms[0]);
  const [trainPercent, setTrainPercent] = useState(50.0);
  const [hmitSearch, setIsHmit] = useState(false);
  const [handleRoot, setHandleRoot] = useState(false);
  const handleOk = useCallback(() => {
    onSelect(algo, trainPercent, hmitSearch, handleRoot);
  }, [onSelect, algo, trainPercent, hmitSearch, handleRoot]);
  return (
    <Dialog
      disableBackdropClick
      disableEscapeKeyDown
      maxWidth="xs"
      aria-labelledby="confirmation-dialog-title"
      open={isOpen}
    >
      <DialogTitle id="confirmation-dialog-title">
        Настройки классификации
      </DialogTitle>
      <DialogContent dividers>
        <AlgorithmInput
          value={algo}
          onChange={setAlgo}
          algorithms={algorithms}
          label="Алгоритм классификации"
        />
        <Typography gutterBottom>Процент обучающей выборки</Typography>
        <SliderInput value={trainPercent} onChange={setTrainPercent} />
        <SwitchInput
          value={hmitSearch}
          onChange={setIsHmit}
          name="isHmit"
          label="Поиск ХМИТов"
        />
        <SwitchInput
          value={handleRoot}
          onChange={setHandleRoot}
          name="handleRoot"
          label="Учитывать корневой элемент"
        />
      </DialogContent>
      <DialogActions>
        <Button autoFocus onClick={handleClose} color="primary">
          Отмена
        </Button>
        <Button onClick={handleOk} color="primary">
          Продолжить
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ClassificationSettings;
