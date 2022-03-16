import { defineNuxtRouteMiddleware } from '#imports'
import { useStore } from '~/stores/main'
import { NotificationType, useNotificationStore } from '~/stores/notification'
import { $api } from '~/composables/useApi'

const anonymousUrls = ['/auth/register', '/auth/login', '/auth/password-reset']

export default defineNuxtRouteMiddleware(async (to) => {
	const store = useStore()
	const notification = useNotificationStore()

	if (process.server) {
		try {
			store.authenticatedUser = await $api<User>('/auth/me')
		} catch (err) {
			if (err instanceof Error && !isUnauthorizedError(err)) {
				console.error(err)
			}
		}
	}

	if (!store.authenticatedUser) {
		if (!anonymousUrls.includes(to.path)) {
			notification.show(
				NotificationType.INFO,
				'Login required',
				'You need to be logged in to access this page',
				10
			)
			return '/auth/login'
		}
	} else {
		if (!store.authenticatedUser.verified && to.path !== '/auth/verify') {
			notification.show(
				NotificationType.INFO,
				'Verification required',
				'You need to verify your account before being able to continue',
				10
			)
			return '/auth/verify'
		}
	}
})
