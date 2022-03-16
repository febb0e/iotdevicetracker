<!-- This example requires Tailwind CSS v2.0+ -->
<template>
	<TransitionRoot as="template" :show="props.open">
		<Dialog as="div" class="fixed z-10 inset-0 overflow-y-auto" @close="close">
			<div
				class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
			>
				<TransitionChild
					as="template"
					enter="ease-out duration-300"
					enter-from="opacity-0"
					enter-to="opacity-100"
					leave="ease-in duration-200"
					leave-from="opacity-100"
					leave-to="opacity-0"
				>
					<DialogOverlay
						class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
					/>
				</TransitionChild>

				<!-- This element is to trick the browser into centering the modal contents. -->
				<span
					class="hidden sm:inline-block sm:align-middle sm:h-screen"
					aria-hidden="true"
					>&#8203;</span
				>
				<TransitionChild
					as="template"
					enter="ease-out duration-300"
					enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
					enter-to="opacity-100 translate-y-0 sm:scale-100"
					leave="ease-in duration-200"
					leave-from="opacity-100 translate-y-0 sm:scale-100"
					leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
				>
					<div
						class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
					>
						<form @submit.prevent="edit">
							<div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
								<div class="sm:flex sm:items-start">
									<div
										class="mt-3 w-full text-center sm:mt-0 sm:ml-4 sm:text-left"
									>
										<DialogTitle
											as="h3"
											class="text-lg leading-6 font-medium text-gray-900"
										>
											Edit User {{ props.user.username }}
										</DialogTitle>
										<div class="mt-2 space-y-6">
											<p class="text-sm text-gray-500">
												Here, you can change the data of a user.
											</p>

											<div>
												<label
													for="username"
													class="block text-sm font-medium text-gray-700"
												>
													Username
												</label>
												<div class="mt-1 relative">
													<input
														v-model="data.username"
														name="username"
														autocomplete="username"
														placeholder="Jane Doe"
														required="true"
														:class="[
															'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
															hasValidationErrors('username')
																? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
																: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
														]"
														:aria-invalid="hasValidationErrors('username')"
														aria-describedby="username-error"
														@input="clearValidationError('username')"
													/>
													<div
														v-show="hasValidationErrors('username')"
														class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
													>
														<ExclamationCircleIcon
															class="h-5 w-5 text-red-500"
															aria-hidden="true"
														/>
													</div>
												</div>
												<p
													v-if="hasValidationErrors('username')"
													id="username-error"
													class="mt-2 text-sm text-red-600"
												>
													{{ validationErrors.username.defaultMessage }}
												</p>
											</div>

											<div>
												<label
													for="email"
													class="block text-sm font-medium text-gray-700"
												>
													Email address
												</label>
												<div class="mt-1 relative">
													<input
														v-model="data.email"
														name="email"
														type="email"
														autocomplete="email"
														placeholder="jane.doe@email.net"
														required="true"
														:class="[
															'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
															hasValidationErrors('email')
																? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
																: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
														]"
														:aria-invalid="hasValidationErrors('email')"
														aria-describedby="email-error"
														@input="clearValidationError('email')"
													/>
													<div
														v-show="hasValidationErrors('email')"
														class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
													>
														<ExclamationCircleIcon
															class="h-5 w-5 text-red-500"
															aria-hidden="true"
														/>
													</div>
												</div>
												<p
													v-if="hasValidationErrors('email')"
													id="email-error"
													class="mt-2 text-sm text-red-600"
												>
													{{ validationErrors.email.defaultMessage }}
												</p>
											</div>

											<div>
												<label
													for="role"
													class="block text-sm font-medium text-gray-700"
													>Role</label
												>
												<select
													v-model="data.role"
													name="role"
													class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
												>
													<option>USER</option>
													<option>SYSADMIN</option>
												</select>
											</div>

											<div>
												<SwitchGroup as="div" class="flex items-center">
													<SwitchLabel as="span" class="mr-3">
														<span class="text-sm font-medium text-gray-700"
															>Verified</span
														>
													</SwitchLabel>
													<Switch
														v-model="data.verified"
														:class="[
															data.verified ? 'bg-indigo-600' : 'bg-gray-200',
															'relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer transition-colors ease-in-out duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500',
														]"
													>
														<span
															aria-hidden="true"
															:class="[
																data.verified
																	? 'translate-x-5'
																	: 'translate-x-0',
																'pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200',
															]"
														/>
													</Switch>
												</SwitchGroup>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div
								class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse"
							>
								<button
									type="submit"
									class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm"
								>
									<span v-if="isEditing">Editing ...</span>
									<span v-else>Edit</span>
								</button>
								<button
									type="button"
									class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
									@click="close"
								>
									Cancel
								</button>
							</div>
						</form>
					</div>
				</TransitionChild>
			</div>
		</Dialog>
	</TransitionRoot>
</template>

<script lang="ts" setup>
	import {
		Dialog,
		DialogOverlay,
		DialogTitle,
		TransitionChild,
		TransitionRoot,
		Switch,
		SwitchGroup,
		SwitchLabel,
	} from '@headlessui/vue'
	import { ExclamationCircleIcon } from '@heroicons/vue/solid/index.js'
	import { useNotificationStore } from '~/stores/notification'
	import { $api, isUnauthorizedError } from '~/composables/useApi'
	import { useStore } from '~~/stores/main'

	interface Props {
		open: boolean
		user: User
	}

	interface Emits {
		(e: 'close'): void
		(e: 'edited', user: User): void
	}

	const props = withDefaults(defineProps<Props>(), { open: false })
	const emit = defineEmits<Emits>()
	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const {
		validationErrors,
		hasValidationErrors,
		clearValidationError,
		fillValidationErrors,
	} = useValidationErrors()
	const isEditing = ref(false)

	const data = reactive({
		username: props.user.username,
		email: props.user.email,
		verified: props.user.verified,
		role: props.user.role,
	})

	async function edit() {
		if (hasValidationErrors() || isEditing.value) return
		isEditing.value = true

		try {
			const user = await $api<User>(`/users/${props.user.id}`, {
				method: 'PATCH',
				body: data,
			})
			// Hanlde the user updating themselves.
			if (user.id === store.authenticatedUser.id) {
				store.authenticatedUser = user
				// Navigate back to dashboard, if the user revoked their own SYSADMIN role.
				if (user.role !== 'SYSADMIN') {
					router.push('/')
				} else {
					emit('edited', user)
				}
			} else {
				emit('edited', user)
			}
			close()
		} catch (err) {
			if (isValidationError(err)) {
				fillValidationErrors(err)
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isEditing.value = false
		}
	}

	function close() {
		emit('close')
	}
</script>
