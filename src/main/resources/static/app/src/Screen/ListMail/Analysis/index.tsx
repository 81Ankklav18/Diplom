import React, { FC } from "react";
import { observer } from "mobx-react";
import ResultDialog from "./ResultDialog";
import LoadingIndicator from "./LoadingIndicator";
import ClassificationSettings from "./ClassificationSettings";
import SimilaritySettings from "./SimilaritySettings";
import { useStore } from "../../../Service/store";

const AnalysisRoot: FC = observer(() => {
  const store = useStore().analysisStore;
  const classificationOpened = store.openedSettings === "CLASSIFICATION";
  const similarityOpened = store.openedSettings === "SIMILARITY";
  return (
    <>
      <LoadingIndicator isOpen={store.isLoading} />
      <SimilaritySettings
        isOpen={similarityOpened}
        onSelect={store.selectSimilarityAlgorithm}
        handleClose={store.resultClose}
      />
      <ClassificationSettings
        isOpen={classificationOpened}
        onSelect={store.selectClassificationAlgorithm}
        handleClose={store.resultClose}
      />
      <ResultDialog handleClose={store.resultClose} content={store.result} />
    </>
  );
});

export default AnalysisRoot;
