export default class Server {
  static request(
    URL: string,
    requestOptions: RequestInit,
    successFunction: (body: string) => void | undefined,
    errorFunction: (error: string) => void
  ) {
    try {
      return fetch(URL, requestOptions).then(async (response) => {
        const body = await response.text();
        if (response.ok) {
          successFunction(body);
        } else {
          errorFunction(body);
        }
      });
    } catch (error) {
      if (error instanceof Error) {
        errorFunction(error.message);
      } else {
        console.log(error);
      }
    }
  }
}
