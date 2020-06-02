import { observable, computed, action, runInAction } from "mobx";
import { Id } from "./types";
import { Mail } from "./queries";

type AnalysisStage = "NONE" | "SETTINGS" | "FETCHING" | "RESULT";
export type ProcessingMethod = "NONE" | "CLASSIFICATION" | "SIMILARITY";
export const algorithms = ["CB0", "NORRIS", "NIAGARA"];
export default class AnalysisStore {
  notifySuccess: () => void;
  notifyError: () => void;
  getSelectedIds: () => Id[];
  constructor(
    getSelectedIds: () => Id[],
    notifySuccess: () => void,
    notifyError: () => void
  ) {
    this.notifySuccess = notifySuccess;
    this.notifyError = notifyError;
    this.getSelectedIds = getSelectedIds;
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
      const data = await Mail.classification(this.getSelectedIds(), algorithmCode);
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
        this.getSelectedIds()[0],
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
