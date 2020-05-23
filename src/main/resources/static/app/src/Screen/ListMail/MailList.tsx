import React, { FC } from "react";
import { observer } from "mobx-react";
import Table from "./Table";
import { useStore } from "../../Service/store";

const MailList: FC = observer(() => {
  const store = useStore();
  return (
    <Table
      rows={store.rows}
      selected={store.selectedIds}
      toggleSelectAll={store.toggleSelectAll}
      selectItem={store.selectItem}
      editItem={store.editMail}
      removeSelected={store.removeSelected}
    />
  );
});

export default MailList;
