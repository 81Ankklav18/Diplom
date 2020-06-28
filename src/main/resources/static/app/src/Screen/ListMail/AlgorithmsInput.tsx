import React, { useCallback, FC, memo, useMemo } from "react";
import {
  RadioGroup,
  Radio,
  FormControlLabel,
  FormControl,
  FormLabel,
} from "@material-ui/core";

interface Props {
  onChange: (value: string) => void;
  value: string;
  algorithms: string[];
  label: string;
}

const AlgorithmInput: FC<Props> = memo(({ onChange, value, algorithms, label }) => {
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
    [algorithms]
  );
  return (
    <FormControl component="fieldset">
      <FormLabel component="legend">{label}</FormLabel>
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
