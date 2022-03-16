import { defineStore } from 'pinia'

type State = {
	sidebarOpen: boolean
	authenticatedUser: User | null
}

export const useStore = defineStore('main', {
	state: () =>
		({
			sidebarOpen: false,
			authenticatedUser: null,
		} as State),
})
