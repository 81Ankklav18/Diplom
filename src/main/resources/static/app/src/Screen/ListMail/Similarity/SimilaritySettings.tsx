import React, { FC, useState, useEffect, useCallback } from "react";
import {
  DialogActions,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  RadioGroup,
  Radio,
  FormControlLabel,
  FormLabel,
  FormControl,
  TextField,
} from "@material-ui/core";
import { algorithms } from "../../../Service/SimilarityAnalysis";

interface Props {
  onSelect: (algorithmCode: string, topN: number) => void;
  handleClose: () => void;
  isOpen: boolean;
}

const SimilaritySettings: FC<Props> = ({ handleClose, onSelect, isOpen }) => {
  const [value, setValue] = useState(algorithms[0]);
  const handleChangeAlgorithm = useCallback((
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setValue((event.target as HTMLInputElement).value);
  }, []);

  const [topN, setTopN] = useState(5);
  const handleChangeText = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    setTopN(Number((event.target as HTMLInputElement).value));
  }, []);

  useEffect(() => {
    if (!isOpen) {
      setTopN(5);
      setValue(algorithms[0]);
    }
  }, [isOpen, value, topN]);
  const handleOk = () => {
    onSelect(value, topN);
  };

  return (
    <Dialog
      disableBackdropClick
      disableEscapeKeyDown
      maxWidth="xs"
      aria-labelledby="confirmation-dialog-title"
      open={isOpen}
    >
      <DialogTitle id="confirmation-dialog-title">
        Выберите параметры
      </DialogTitle>
      <DialogContent dividers>
        <TextField
          onChange={handleChangeText}
          value={topN}
          error={topN < 1}
          label="Топ N схожих"
          type="number"
          name="topN"
          variant="outlined"
          fullWidth
          margin="dense"
          size="small"
        />
        <FormControl component="fieldset">
          <FormLabel component="legend">Алгоритм обработки</FormLabel>
          <RadioGroup
            aria-label="algorithm"
            name="algorithm"
            value={value}
            onChange={handleChangeAlgorithm}
          >
            {algorithms.map((option) => (
              <FormControlLabel
                value={option}
                key={option}
                control={<Radio />}
                label={option}
              />
            ))}
          </RadioGroup>
        </FormControl>
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

export default SimilaritySettings;
