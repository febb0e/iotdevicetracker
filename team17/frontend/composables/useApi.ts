import { useStore } from '~~/stores/main'
import { NotificationType, useNotificationStore } from '~~/stores/notification'
import { murmurHashV3 } from 'murmurhash-es'
import { FetchError } from 'ohmyfetch'

type RequestOptions = {
	method?: string
	body?: Record<string, unknown>
	pick?: string[]
	params?: Record<string, unknown>
}

export function isUnauthorizedError(err: Error): err is FetchError {
	return err instanceof FetchError && err.response?.status === 401
}

export function isValidationError(err: Error): err is FetchError {
	return err instanceof FetchError && err.response?.status === 422
}

function getBaseURL() {
	const config = useRuntimeConfig() as { API_URL: string }
	return process.server ? config.API_URL : '/api-proxy'
}

export const $api = async <Result = unknown>(
	endpoint: string,
	opts?: RequestOptions
) => {
	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const baseURL = getBaseURL()
	const headers = {} as Record<string, string>

	if (process.server) {
		Object.assign(headers, useRequestHeaders(['cookie']))
	}

	if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(opts?.method)) {
		const { token } = await $fetch<{ token: string }>('/auth/csrf', {
			baseURL,
			headers,
		})
		headers['x-xsrf-token'] = token
	}

	return $fetch<Result>(endpoint, {
		method: opts?.method,
		body: opts?.body,
		baseURL,
		headers,
		params: opts?.params,
		async onResponseError({ response }) {
			if (store.authenticatedUser && response.status === 401) {
				store.authenticatedUser = null
				notification.show(
					NotificationType.WARN,
					'You have been logged out',
					'Your session expired, please login again',
					10
				)
				await router.push('/auth/login')
			}
		},
	})
}

export const useApi = async <Result = unknown>(
	endpoint: string,
	opts?: RequestOptions
) => {
	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const baseURL = getBaseURL()
	const headers = {} as Record<string, string>

	if (process.server) {
		Object.assign(headers, useRequestHeaders(['cookie']))
	}

	if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(opts?.method)) {
		const { token } = await $fetch<{ token: string }>('/auth/csrf', {
			baseURL,
			headers,
		})
		headers['x-xsrf-token'] = token
	}

	return useFetch<string, Result>(endpoint, {
		method: opts?.method,
		body: opts?.body,
		baseURL,
		headers,
		params: opts?.params,
		// The default key implementation includes the baseURL in the hasing process.
		// As this is different for server and client, the default implementation leads to different
		// keys, resulting in hydration errors.
		key: '$api-' + murmurHashV3(JSON.stringify({ endpoint, opts })).toString(),
		async onResponseError({ response }) {
			if (store.authenticatedUser && response.status === 401) {
				store.authenticatedUser = null
				notification.show(
					NotificationType.WARN,
					'You have been logged out',
					'Your session expired, please login again',
					10
				)
				await router.push('/auth/login')
			}
		},
	})
}
