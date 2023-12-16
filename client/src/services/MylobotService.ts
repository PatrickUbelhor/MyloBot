import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

type HttpResponse<T> = Promise<AxiosResponse<T>>;

class MylobotService {

	private http: AxiosInstance = axios.create({
		// baseURL: process.env.REACT_APP_SERVER_URL
		baseURL: '/api/v1'
	});


	pause = (): HttpResponse<any> => {
		const url = '/pause';
		return this.http.post(url);
	};

	unpause = (): HttpResponse<any> => {
		const url = '/unpause';
		return this.http.post(url);
	};

	skip = (quantity: number = 1): HttpResponse<any> => {
		const url = '/skip';
		const config: AxiosRequestConfig = {
			params: {
				quantity: quantity
			}
		};
		return this.http.post(url, null, config);
	};

	skipAll = (): HttpResponse<void> => {
		const url = '/skipall';
		return this.http.post(url);
	};

	play = (link: string): HttpResponse<void> => {
		const url = '/play';
		return this.http.post(url, link);
	};

}
