import { action, runInAction } from "mobx";
import { Mail } from "./queries";
import AnalysisBase from "./AnalysisBase";

export const algorithms = ["CB0", "NORRIS", "NIAGARA"];
export default class SimilarityAnalysis extends AnalysisBase<string> {
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
