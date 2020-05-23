import axios, { AxiosPromise as R, AxiosRequestConfig, Method } from "axios";
import { MailListItem, Id, MailEditItemDto, MailEditItem } from "./types";

export const requestOptions = (
  url: string,
  method: Method,
  data?: object | FormData
): AxiosRequestConfig => ({
  url,
  method,
  data,
});

export const Mail = {
  getAll: (): R<MailListItem[]> =>
    axios.request<MailListItem[]>(requestOptions("/mail", "GET")),
  getItem: (id: Id): R<MailEditItemDto> =>
    axios.request<MailEditItemDto>(requestOptions(`/mail/${id}`, "GET")),
  analyze: (id: Id[]): R<MailEditItemDto> =>
    axios.request<MailEditItemDto>(requestOptions("/mail/analyze", "POST", id)),
  create: (item: MailEditItemDto): R<MailEditItemDto> =>
    axios.request<MailEditItemDto>(requestOptions("/mail", "POST", item)),
  update: (item: MailEditItemDto): R<MailEditItemDto> =>
    axios.request<MailEditItemDto>(
      requestOptions(`/mail/${item.id}`, "PUT", item)
    ),
  remove: (ids: Id[]): R<void> =>
    axios.request(requestOptions("/mail", "DELETE", ids)),
  toDto: (model: MailEditItem): MailEditItemDto => ({
    ...model,
    date: model.date.toISOString(),
  }),
  toModel: (model: MailEditItemDto): MailEditItem => ({
    ...model,
    date: new Date(model.date),
  }),
};
