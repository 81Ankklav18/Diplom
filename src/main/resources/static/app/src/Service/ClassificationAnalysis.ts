import { action, runInAction } from "mobx";
import { Mail } from "./queries";
import AnalysisBase from "./AnalysisBase";
import { ClassificationResult } from "./types";

export const algorithms = ["CB0", "NORRIS", "NIAGARA"];
export default class ClassificationAnalysis extends AnalysisBase<
  ClassificationResult
> {
  @action
  selectClassificationAlgorithm = async (
    algorithmCode: string,
    trainPercent: number
  ) => {
    this.stage = "FETCHING";
    try {
      const data = await Mail.classification(
        this.getSelectedIds(),
        algorithmCode,
        trainPercent
      );
      runInAction(() => {
        this.result = data.data;
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
