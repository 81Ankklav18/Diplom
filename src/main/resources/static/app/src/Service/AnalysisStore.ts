import { observable, computed, action, runInAction } from "mobx";
import { Id } from "./types";
import { Mail } from "./queries";

type AnalysisStage = "NONE" | "SETTINGS" | "FETCHING" | "RESULT";
export type ProcessingMethod = "NONE" | "CLASSIFICATION" | "SIMILARITY";
export const algorithms = ["CB0", "NORRIS", "NIAGARA"];
export default class AnalysisStore {
  notifySuccess: () => void;
  notifyError: () => void;
  selectedIds: Id[];
  constructor(
    selectedIds: Id[],
    notifySuccess: () => void,
    notifyError: () => void
  ) {
    this.notifySuccess = notifySuccess;
    this.notifyError = notifyError;
    this.selectedIds = selectedIds;
  }
  @observable stage: AnalysisStage = "NONE";
  @observable processing: ProcessingMethod = "NONE";
  @observable result: string | null = null;
  @computed get isLoading() {
    return this.stage === "FETCHING";
  }
  @computed get openedSettings(): ProcessingMethod {
    return this.stage === "SETTINGS" ? this.processing : "NONE";
  }
  @computed get isSimilarityDisabled() {
    return this.selectedIds.length !== 1;
  }
  @action
  resultClose = () => {
    this.result = null;
    this.stage = "NONE";
    this.processing = "NONE";
  };
  @action
  toClassification = () => {
    this.processing = "CLASSIFICATION";
    this.stage = "SETTINGS";
    console.log(this.openedSettings);
  };
  @action
  selectClassificationAlgorithm = async (algorithmCode: string) => {
    this.stage = "FETCHING";
    try {
      const data = await Mail.classification(this.selectedIds, algorithmCode);
      runInAction(() => {
        this.result = JSON.stringify(data.data, null, "\t");
        this.notifySuccess();
        this.stage = "RESULT";
      });
    } catch (error) {
      runInAction(() => {
        this.notifyError();
        this.resultClose();
      });
    }
  };
  @action
  toSimilarity = () => {
    this.processing = "SIMILARITY";
    this.stage = "SETTINGS";
  };
  @action
  selectSimilarityAlgorithm = async (algorithmCode: string, topN: number) => {
    this.stage = "FETCHING";
    try {
      const data = await Mail.similarity(
        this.selectedIds[0],
        algorithmCode,
        topN
      );
      runInAction(() => {
        this.result = JSON.stringify(data.data, null, "\t");
        this.notifySuccess();
        this.stage = "RESULT";
      });
    } catch (error) {
      runInAction(() => {
        this.notifyError();
        this.resultClose();
      });
    }
  };
}
