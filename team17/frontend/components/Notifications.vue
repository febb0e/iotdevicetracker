<template>
	<!-- Global notification live region, render this permanently at the end of the document -->
	<div
		aria-live="assertive"
		class="fixed inset-x-0 bottom-0 flex items-end px-4 py-6 pointer-events-none sm:p-6 sm:items-start z-60"
	>
		<div class="w-full flex flex-col items-center space-y-4 sm:items-end">
			<!-- Notification panel, dynamically insert this into the live region when it needs to be displayed -->
			<transition
				enter-active-class="transform ease-out duration-300 transition"
				enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
				enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
				leave-active-class="transition ease-in duration-100"
				leave-from-class="opacity-100"
				leave-to-class="opacity-0"
			>
				<div
					v-if="shouldShow"
					class="max-w-md w-full bg-white shadow-lg rounded-lg pointer-events-auto ring-1 ring-black ring-opacity-5 overflow-hidden"
				>
					<div class="p-4">
						<div class="flex items-start">
							<div class="flex-shrink-0">
								<CheckCircleIcon
									v-if="type === NotificationType.SUCCESS"
									class="h-6 w-6 text-green-400"
									aria-hidden="true"
								/>
								<InformationCircleIcon
									v-else-if="type === NotificationType.INFO"
									class="h-6 w-6 text-blue-400"
									aria-hidden="true"
								></InformationCircleIcon>
								<ExclamationCircleIcon
									v-else-if="type === NotificationType.WARN"
									class="h-6 w-6 text-yellow-400"
									aria-hidden="true"
								></ExclamationCircleIcon>
								<ExclamationIcon
									v-else-if="type === NotificationType.ERROR"
									class="h-6 w-6 text-red-400"
									aria-hidden="true"
								></ExclamationIcon>
							</div>
							<div class="ml-3 w-0 flex-1 pt-0.5">
								<p class="text-sm font-medium text-gray-900">
									{{ title }}
								</p>
								<p class="mt-1 text-sm text-gray-500">
									{{ message }}
								</p>
							</div>
							<div class="ml-4 flex-shrink-0 flex">
								<button
									@click="store.hide"
									class="bg-white rounded-md inline-flex text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
								>
									<span class="sr-only">Close</span>
									<XIcon class="h-5 w-5" aria-hidden="true" />
								</button>
							</div>
						</div>
					</div>
				</div>
			</transition>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import {
		CheckCircleIcon,
		InformationCircleIcon,
		ExclamationCircleIcon,
		ExclamationIcon,
	} from '@heroicons/vue/outline/index.js'
	import { XIcon } from '@heroicons/vue/solid/index.js'
	import { useNotificationStore } from '~/stores/notification'
	import { storeToRefs } from 'pinia'
	import { NotificationType } from '~/stores/notification'

	const store = useNotificationStore()
	const { shouldShow, type, title, message } = storeToRefs(store)
</script>
