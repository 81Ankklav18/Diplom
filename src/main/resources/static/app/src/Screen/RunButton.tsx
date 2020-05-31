import React, {
  FC,
  useRef,
  useEffect,
  useCallback,
  useState,
  MouseEvent,
  KeyboardEvent,
} from "react";
import {
  IconButton,
  Tooltip,
  Popper,
  Grow,
  Paper,
  ClickAwayListener,
  MenuItem,
  MenuList,
} from "@material-ui/core";
import PlayCircleOutline from "@material-ui/icons/PlayCircleOutline";

interface Props {
  startClassification: () => void;
  startSimilaritySearch: () => void;
  isSimilarityDisabled: boolean;
}

const RunButton: FC<Props> = ({
  startClassification,
  startSimilaritySearch,
  isSimilarityDisabled,
}) => {
  const [open, setOpen] = useState(false);
  const anchorRef = useRef<HTMLButtonElement>(null);
  const handleToggle = useCallback(() => setOpen((prevOpen) => !prevOpen), []);
  const handleClose = useCallback(
    (event?: MouseEvent<EventTarget>) => {
      if (
        !event ||
        (anchorRef.current &&
        anchorRef.current.contains(event.target as HTMLElement))
      ) {
        return;
      }
      setOpen(false);
    },
    [anchorRef]
  );
  const onSimilarityClick = useCallback(() => {
    startSimilaritySearch();
    handleClose();
  }, [handleClose, startSimilaritySearch]);
  const onClassificationClick = useCallback(() => {
    startClassification();
    handleClose();
  }, [handleClose, startClassification]);
  const handleListKeyDown = useCallback((event: KeyboardEvent) => {
    if (event.key === "Tab") {
      event.preventDefault();
      setOpen(false);
    }
  }, []);
  const prevOpen = useRef(open);
  useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current!.focus();
    }
    prevOpen.current = open;
  }, [open, prevOpen, anchorRef]);
  return (
    <>
      <Tooltip title="Анализ данных">
        <IconButton
          ref={anchorRef}
          aria-label="start processing"
          color="inherit"
          onClick={handleToggle}
        >
          <PlayCircleOutline />
        </IconButton>
      </Tooltip>
      <Popper open={open} anchorEl={anchorRef.current} transition disablePortal>
        {({ TransitionProps, placement }) => (
          <Grow
            {...TransitionProps}
            style={{
              transformOrigin:
                placement === "bottom" ? "center top" : "center bottom",
            }}
          >
            <Paper>
              <ClickAwayListener onClickAway={handleClose}>
                <MenuList autoFocusItem={open} onKeyDown={handleListKeyDown}>
                  <MenuItem onClick={onClassificationClick}>
                    Классификация
                  </MenuItem>
                  <MenuItem
                    onClick={onSimilarityClick}
                    disabled={isSimilarityDisabled}
                  >
                    Поиск сходства
                  </MenuItem>
                </MenuList>
              </ClickAwayListener>
            </Paper>
          </Grow>
        )}
      </Popper>
    </>
  );
};

export default RunButton;
