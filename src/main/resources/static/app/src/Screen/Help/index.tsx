import React, { FC } from "react";
import { observer } from "mobx-react";
import HelpDialog from "./HelpDialog";
import { useStore } from "../../Service/store";

const HelpRoot: FC = observer(() => {
  const store = useStore().helpStore;
  return (
      <HelpDialog
        isOpen={store.isOpen}
        handleClose={store.close}
      />
  );
});

export default HelpRoot;
