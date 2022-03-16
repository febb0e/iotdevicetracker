import { defineStore } from 'pinia'

export enum NotificationType {
	SUCCESS,
	INFO,
	WARN,
	ERROR,
}

let timeout: ReturnType<typeof setTimeout> | undefined

export const useNotificationStore = defineStore('notification', {
	state: () => ({
		shouldShow: false,
		title: '',
		message: '',
		type: NotificationType.INFO,
	}),
	actions: {
		show(
			type: NotificationType,
			title: string,
			message: string,
			seconds: number
		) {
			this.hide()

			this.type = type
			this.title = title
			this.message = message

			this.shouldShow = true
			timeout = setTimeout(() => {
				this.shouldShow = false
				timeout = undefined
			}, seconds * 1000)
		},
		internalServerError() {
			this.show(
				NotificationType.ERROR,
				'Internal Server Error',
				'Please try again later and contact the administrator if the issue persists.',
				10
			)
		},
		hide() {
			if (timeout) {
				clearTimeout(timeout)
				timeout = undefined
			}
			this.shouldShow = false
		},
	},
})
