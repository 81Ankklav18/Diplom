import React, { FC } from "react";
import { observer } from "mobx-react";
import ResultDialog from "./ResultDialog";
import LoadingIndicator from "../LoadingIndicator";
import ClassificationSettings from "./ClassificationSettings";
import { useStore } from "../../../Service/store";

const AnalysisRoot: FC = observer(() => {
  const store = useStore().classificationStore;
  return (
    <>
      <ClassificationSettings
        isOpen={store.isOpenedSettings}
        onSelect={store.selectClassificationAlgorithm}
        handleClose={store.resultClose}
      />
      <LoadingIndicator isOpen={store.isLoading} />
      <ResultDialog
        isOpen={store.isOpenedResult}
        handleClose={store.resultClose}
        content={store.result}
      />
    </>
  );
});

export default AnalysisRoot;
