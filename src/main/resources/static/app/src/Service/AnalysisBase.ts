import { observable, computed, action } from "mobx";
import { Id } from "./types";
import BaseService from "./BaseService";

type AnalysisStage = "NONE" | "SETTINGS" | "FETCHING" | "RESULT";
export default class AnalysisBase<T> extends BaseService {
  getSelectedIds: () => Id[];
  constructor(
    getSelectedIds: () => Id[],
    notifySuccess: () => void,
    notifyError: () => void
  ) {
    super(notifySuccess, notifyError);
    this.getSelectedIds = getSelectedIds;
  }
  @observable stage: AnalysisStage = "NONE";
  @observable result: T | null = null;
  @computed get isLoading() {
    return this.stage === "FETCHING";
  }
  @computed get isOpenedSettings(): boolean {
    return this.stage === "SETTINGS";
  }
  @computed get isOpenedResult(): boolean {
    return this.stage === "RESULT";
  }
  @action
  resultClose = () => {
    this.result = null;
    this.stage = "NONE";
  };
  @action
  toSettings = () => (this.stage = "SETTINGS");
}
