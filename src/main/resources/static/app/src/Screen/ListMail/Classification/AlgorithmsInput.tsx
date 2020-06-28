import React, { useCallback, FC, memo, useMemo } from "react";
import {
  RadioGroup,
  Radio,
  FormControlLabel,
  FormControl,
  FormLabel,
} from "@material-ui/core";
import { algorithms } from "../../../Service/ClassificationAnalysis";

interface Props {
  onChange: (value: string) => void;
  value: string;
}

const AlgorithmInput: FC<Props> = memo(({ onChange, value }) => {
  const handleChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) =>
      onChange(event.target.value),
    [onChange]
  );
  const radioItems = useMemo(
    () =>
      algorithms.map((option) => (
        <FormControlLabel
          value={option}
          key={option}
          control={<Radio />}
          label={option}
        />
      )),
    []
  );
  return (
    <FormControl component="fieldset">
      <FormLabel component="legend">Алгоритм классификации</FormLabel>
      <RadioGroup
        aria-label="algorithm"
        name="algorithm"
        value={value}
        onChange={handleChange}
      >
        {radioItems}
      </RadioGroup>
    </FormControl>
  );
});

export default AlgorithmInput;
