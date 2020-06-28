import { action, runInAction } from "mobx";
import { Mail } from "./queries";
import AnalysisBase from "./AnalysisBase";
import { ClassificationResult } from "./types";
import { saveAsFile, contentType } from "./contentLoad";

export const algorithms = ["CB0", "NORRIS", "NIAGARA"];
export default class ClassificationAnalysis extends AnalysisBase<
  ClassificationResult
> {
  @action
  selectClassificationAlgorithm = async (
    algorithmCode: string,
    trainPercent: number,
    hmitSearch: boolean,
    handleRoot: boolean,
  ) => {
    this.stage = "FETCHING";
    try {
      const data = await Mail.classification(
        this.getSelectedIds(),
        algorithmCode,
        trainPercent,
        hmitSearch,
        handleRoot
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
  @action
  saveSemiLatticeAsFile = () => {
    if (this.result === null) return;
    try {
      saveAsFile(
        "semilattice.json",
        JSON.stringify(this.result.semiLatticeViewDTO, null, "\t"),
        contentType.json
      );
      runInAction(this.notifySuccess);
    } catch (error) {
      runInAction(this.notifyError);
    }
  };
}
