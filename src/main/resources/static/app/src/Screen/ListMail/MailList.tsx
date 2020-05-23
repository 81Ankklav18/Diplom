import React, { FC } from "react";
import Table from "./Table";
import { Paper, Box, Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { useStore } from "../../Service/store";
import { observer } from "mobx-react";

const MailList: FC = observer(() => {
  const store = useStore();
  return (
    <>
      <Box m={2}>
        <Paper>
          <Button
            variant="contained"
            color="primary"
            onClick={store.createMail}
          >
            Добавить письмо <AddIcon />
          </Button>
          <Button
            variant="contained"
            color="secondary"
            onClick={store.reloadTable}
          >
            Перезагрузить данные
          </Button>
          <Button
            variant="contained"
            color="secondary"
            onClick={store.analyze}
          >
            Анализ данных
          </Button>
        </Paper>
      </Box>
      <Table
        rows={store.rows}
        selected={store.selectedIds}
        toggleSelectAll={store.toggleSelectAll}
        selectItem={store.selectItem}
        editItem={store.editMail}
        removeSelected={store.removeSelected}
      />
    </>
  );
});

export default MailList;
