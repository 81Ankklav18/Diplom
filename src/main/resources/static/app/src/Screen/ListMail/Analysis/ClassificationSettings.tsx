import React, { FC, useState } from "react";
import {
  DialogActions,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  RadioGroup,
  Radio,
  FormControlLabel,
  FormControl,
  FormLabel,
} from "@material-ui/core";
import { algorithms } from "../../../Service/AnalysisStore";

interface Props {
  onSelect: (algorithmCode: string) => void;
  handleClose: () => void;
  isOpen: boolean;
}

const ClassificationSettings: FC<Props> = ({
  handleClose,
  onSelect,
  isOpen,
}) => {
  const [value, setValue] = useState(algorithms[0]);
  const handleOk = () => {
    onSelect(value);
  };
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setValue((event.target as HTMLInputElement).value);
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
        Настройки классификации
      </DialogTitle>
      <DialogContent dividers>
        <FormControl component="fieldset">
          <FormLabel component="legend">Алгоритм классификации</FormLabel>
          <RadioGroup
            aria-label="algorithm"
            name="algorithm"
            value={value}
            onChange={handleChange}
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

export default ClassificationSettings;
