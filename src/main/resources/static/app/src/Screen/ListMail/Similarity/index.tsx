import React, { FC } from "react";
import { observer } from "mobx-react";
import SimilarityResultDialog from "./SimilarityResultDialog";
import LoadingIndicator from "../LoadingIndicator";
import SimilaritySettings from "./SimilaritySettings";
import { useStore } from "../../../Service/store";

const AnalysisRoot: FC = observer(() => {
  const store = useStore().similarityStore;
  return (
    <>
      <SimilaritySettings
        isOpen={store.isOpenedSettings}
        onSelect={store.selectSimilarityAlgorithm}
        handleClose={store.resultClose}
      />
      <LoadingIndicator isOpen={store.isLoading} />
      <SimilarityResultDialog
        isOpen={store.isOpenedResult}
        handleClose={store.resultClose}
        content={store.result}
      />
    </>
  );
});

export default AnalysisRoot;
