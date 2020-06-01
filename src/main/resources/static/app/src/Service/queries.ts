import axios, { AxiosPromise as R, Method } from "axios";
import { MailListItem, Id, MailEditItemDto, MailEditItem, ClassificationResult, SimilarityResult } from "./types";

const fetcher = <T>(
  url: string,
  method: Method,
  data?: object | FormData
): R<T> =>
  axios.request<T>({
    url,
    method,
    data,
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

  classification: (id: Id[], method: string) =>
    fetcher<ClassificationResult>("/mail/classification", "POST", { id, method }),
  similarity: (id: Id, method: string, topN: number) =>
    fetcher<SimilarityResult>("/mail/similarity", "POST", { id, method, topN }),
};
