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
											Edit User {{ props.user.user.username }}
										</DialogTitle>
										<div class="mt-2 space-y-6">
											<p class="text-sm text-gray-500">
												Here, you can change the role of the user.
											</p>

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
													aria-describedby="role-description"
												>
													<option>OBSERVER</option>
													<option>ADMIN</option>
												</select>
												<p
													id="role-description"
													class="mt-2 text-sm text-gray-500"
												>
													OBSERVER allows the user to view the metrics of all
													devices in this group.<br />
													ADMIN additionally allows managing users and devices,
													as well as editing and deleting this group.
												</p>
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
	} from '@headlessui/vue'
	import { useNotificationStore } from '~/stores/notification'
	import { $api, isUnauthorizedError } from '~/composables/useApi'

	interface Props {
		open: boolean
		user: DeviceGroupUser
	}

	interface Emits {
		(e: 'close'): void
		(e: 'edited', dgu: DeviceGroupUser): void
	}

	const props = withDefaults(defineProps<Props>(), { open: false })
	const emit = defineEmits<Emits>()
	const notification = useNotificationStore()
	const isEditing = ref(false)

	const data = reactive({
		role: props.user.role,
	})

	async function edit() {
		if (isEditing.value) return
		isEditing.value = true

		try {
			const dgu = await $api<DeviceGroupUser>(
				`/device-groups/${props.user.id.deviceGroupId}/users/${props.user.id.userId}`,
				{
					method: 'PATCH',
					body: data,
				}
			)
			emit('edited', dgu)
			close()
		} catch (err) {
			if (!isUnauthorizedError(err)) {
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
