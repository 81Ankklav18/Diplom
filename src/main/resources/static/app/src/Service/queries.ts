import axios, { AxiosPromise as R, Method, AxiosRequestConfig } from "axios";
import {
  MailListItem,
  Id,
  MailEditItemDto,
  MailEditItem,
  ClassificationResult,
  SimilarityResult,
} from "./types";

const fetcher = <T>(
  url: string,
  method: Method,
  data?: object | FormData
): R<T> => {
  const config: AxiosRequestConfig = {
    url,
    method,
    data,
  };
  if (data instanceof FormData) {
    config.headers = {
      "Content-Type": "multipart/form-data",
    };
  }
  return axios.request<T>(config);
};
const fetchBlob = (url: string, method: Method, data?: object | FormData): R =>
  axios.request({
    url,
    method,
    data,
    responseType: "blob",
  });

export const Mail = {
  getAll: () => fetcher<MailListItem[]>("/mail", "GET"),
  getItem: (id: Id) => fetcher<MailEditItemDto>(`/mail/${id}`, "GET"),
  create: (item: MailEditItemDto) =>
    fetcher<MailEditItemDto>("/mail", "POST", item),
  update: (item: MailEditItemDto) =>
    fetcher<MailEditItemDto>(`/mail/${item.id}`, "PUT", item),
  remove: (ids: Id[]) => fetcher("/mail", "DELETE", ids),
  toDto: (model: MailEditItem): MailEditItemDto => ({
    ...model,
    date: model.date.toISOString(),
  }),
  toModel: (model: MailEditItemDto): MailEditItem => ({
    ...model,
    date: new Date(model.date),
  }),

  classification: (
    id: Id[],
    method: string,
    trainPercent: number,
    hmitSearch: boolean,
    handleRoot: boolean
  ) =>
    fetcher<ClassificationResult>("/mail/classification", "POST", {
      id,
      method,
      trainPercent,
      hmitSearch,
      handleRoot
    }),
  similarity: (id: Id, method: string, topN: number) =>
    fetcher<SimilarityResult>("/mail/similarity", "POST", { id, method, topN }),
  importDataJson: (data: FormData) =>
    fetcher<void>("/mail/import/json", "POST", data),
  importDataCsv: (data: FormData) =>
    fetcher<void>("/mail/import/csv", "POST", data),

  exportDataJson: () => fetchBlob("/mail/export/json", "GET"),
  exportDataCsv: () => fetchBlob("/mail/export/csv", "GET"),
};
