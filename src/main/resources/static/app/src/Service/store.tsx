import React, { FC } from "react";
import { configure } from "mobx";
import { useLocalStore } from "mobx-react";
import Mails from "./MailStore";

configure({ enforceActions: "observed" });

const StoreContext = React.createContext<Mails | null>(null);

export const StoreProvider: FC = ({ children }) => {
  const store = useLocalStore(() => new Mails());
  return (
    <StoreContext.Provider value={store}>{children}</StoreContext.Provider>
  );
};

export const useStore = () => {
  const store = React.useContext(StoreContext);
  if (!store) {
    throw new Error("useStore must be used within a StoreProvider.");
  }
  return store;
};
