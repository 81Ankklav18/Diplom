import React, { useCallback, FC, memo } from "react";
import { FormControlLabel, Switch } from "@material-ui/core";
interface Props {
  onChange: (value: boolean) => void;
  value: boolean;
  name: string;
  label: string;
}

const SwitchInput: FC<Props> = memo(({ onChange, value, name, label }) => {
  const handleChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) =>
      onChange(event.target.checked),
    [onChange]
  );
  return (
    <FormControlLabel
      control={
        <Switch
          checked={value}
          onChange={handleChange}
          name={name}
        />
      }
      label={label}
      color="primary"
    />
  );
});

export default SwitchInput;
