<template>
	<div class="mt-6">
		<!-- HEADLINE: name, role, identifier and modal buttons -->
		<div
			class="flex items-center justify-between mb-4 pb-5 border-b border-gray-200"
		>
			<div>
				<h3 class="text-lg leading-6 font-medium text-gray-900">
					{{ device.name }}
					<span
						class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-sm font-medium bg-green-100 rounded-full"
						>{{ device.role }}</span
					>
				</h3>
				<p class="text-sm text-gray-500">{{ device.identifier }}</p>
			</div>
			<div class="flex-grow"></div>
			<button
				v-if="device.role === 'ADMIN'"
				type="button"
				class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
				@click="editModalOpen = true"
			>
				Edit
			</button>
			<button
				v-if="device.role === 'ADMIN'"
				type="button"
				class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3"
				@click="deleteModalOpen = true"
			>
				Delete
			</button>
		</div>

		<!-- CONTROLS -->
		<div class="w-full flex flex-row mb-2 items-center">
			<div class="mr-2">
				<label for="timeframe" class="block text-sm font-medium text-gray-700"
					>Timeframe</label
				>
				<select
					v-model="timeFrame"
					name="timeframe"
					class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
				>
					<option v-for="option in Object.values(TimeFrame)" :key="option">
						{{ option }}
					</option>
				</select>
			</div>
			<div class="mr-2">
				<label for="refreshtime" class="block text-sm font-medium text-gray-700"
					>Refresh Interval</label
				>
				<select
					v-model="refreshInterval"
					name="refreshtime"
					class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
				>
					<option
						v-for="option in Object.values(RefreshInterval)"
						:key="option"
					>
						{{ option }}
					</option>
				</select>
			</div>
			<Listbox as="div" v-model="query.fields">
				<ListboxLabel class="block text-sm font-medium text-gray-700">
					Fields
				</ListboxLabel>
				<div class="mt-1 relative">
					<ListboxButton
						class="bg-white relative w-full border border-gray-300 rounded-md shadow-sm pl-3 pr-10 py-2 text-left cursor-default focus:outline-none focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
					>
						<span class="block">{{ query.fields.join(', ') }}</span>
						<span
							class="absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none"
						>
							<SelectorIcon class="h-5 w-5 text-gray-400" aria-hidden="true" />
						</span>
					</ListboxButton>

					<transition
						leave-active-class="transition ease-in duration-100"
						leave-from-class="opacity-100"
						leave-to-class="opacity-0"
					>
						<ListboxOptions
							class="min-w-fit absolute z-10 mt-1 bg-white shadow-lg max-h-60 rounded-md py-1 text-base ring-1 ring-black ring-opacity-5 overflow-auto focus:outline-none sm:text-sm"
						>
							<ListboxOption
								as="template"
								v-for="(field, index) in availableFields"
								:key="index"
								:value="
									isFieldSelected(field)
										? query.fields.filter((x) => x !== field)
										: [...query.fields, field]
								"
								v-slot="{ active }"
							>
								<li
									:class="[
										active ? 'text-white bg-indigo-600' : 'text-gray-900',
										'cursor-default select-none relative py-2 pl-3 pr-9',
									]"
								>
									<span
										:class="[
											isFieldSelected(field) ? 'font-semibold' : 'font-normal',
											'block',
										]"
									>
										{{ field }}
									</span>

									<span
										v-if="isFieldSelected(field)"
										:class="[
											active ? 'text-white' : 'text-indigo-600',
											'absolute inset-y-0 right-0 flex items-center pr-4',
										]"
									>
										<CheckIcon class="h-5 w-5" aria-hidden="true" />
									</span>
								</li>
							</ListboxOption>
						</ListboxOptions>
					</transition>
				</div>
			</Listbox>

			<div class="flex-grow"></div>
			<div>
				<button
					class="order-0 px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
					@click="fetchMetrics"
				>
					Reload
				</button>
			</div>
		</div>
		<div
			style="margin-top: 50px; flex-flow: wrap-reverse; justify-content: center"
		>
			<div class="" style="flex-flow: column; align-items: stretch">
				<client-only>
					<apexchart
						v-show="query.fields.length > 0"
						width="100%"
						type="area"
						:options="chartOptions"
						:series="series"
					/>
					<div
						v-show="query.fields.length === 0"
						class="mt-6 rounded-md bg-yellow-50 p-4"
					>
						<div class="flex">
							<div class="flex-shrink-0">
								<ExclamationIcon
									class="h-5 w-5 text-yellow-400"
									aria-hidden="true"
								/>
							</div>
							<div class="ml-3">
								<h3 class="text-sm font-medium text-yellow-800">
									Missing data
								</h3>
								<div class="mt-2 text-sm text-yellow-700">
									<p>
										There are no metrics for this device for the given
										Timeframe. Try to increase the Timeframe.
									</p>
								</div>
							</div>
						</div>
					</div>
				</client-only>
			</div>
			<!-- <div style="display: flex; flex-flow: column; align-items: center">
				<h1
					class="border-b-4 w-full mb-2 column-right text-4xl font-medium rounded-md block"
					style="margin-top: auto; bottom: 0"
				>
					Location
				</h1>
				<client-only>
					<l-map
						class="column-right"
						v-model:zoom="zoom"
						:zoomAnimation="true"
						:options="{ zoomSnap: 0.1, zoomDelta: 0.5 }"
						:maxZoom="18"
						:minZoom="2"
						:center="currentPosition"
						style="
							order: 1;
							flex-basis: ;
							justify-content: center;
							min-height: 20vh;
							min-width: 30vh;
							align-items: stretch;
						"
					>
						<l-tile-layer
							url="https://{s}.tile.openstreetmap.de/{z}/{x}/{y}.png"
						/>
						<l-circle-marker
							v-if="
								positionmode == 'Current Position' || positionmode == 'History'
							"
							:lat-lng="currentPosition"
							color="#4338ca"
							:radius="8"
						/>
						<l-polyline
							v-if="positionmode == 'History'"
							:lat-lngs="path"
							color="#4338ca"
							:weight="5"
							:opacity="0.8"
						/>
					</l-map>
				</client-only>
				<button
					@click="switchPositionMode"
					style="order: 2; flex: auto"
					class="querySelection-slim"
				>
					<label for="positionmode" class="h-5 w-5 text-white">{{
						positionmode
					}}</label>
				</button>
			</div> -->
		</div>
		<EditDeviceModal
			:open="editModalOpen"
			:device="device"
			@edited="onEdit"
			@close="editModalOpen = false"
		></EditDeviceModal>
		<DeleteDeviceModal
			:open="deleteModalOpen"
			:device="device"
			@deleted="onDelete"
			@close="deleteModalOpen = false"
		></DeleteDeviceModal>
	</div>
</template>

<script lang="ts" setup>
	import 'leaflet/dist/leaflet.css'
	import { CheckIcon, SelectorIcon } from '@heroicons/vue/outline/index.js'
	import { ExclamationIcon } from '@heroicons/vue/solid/index.js'
	import {
		Listbox,
		ListboxButton,
		ListboxLabel,
		ListboxOption,
		ListboxOptions,
	} from '@headlessui/vue'
	import { NotificationType, useNotificationStore } from '~/stores/notification'
	import { $api } from '~/composables/useApi'
	import type { ApexOptions } from 'apexcharts'

	// COMPOSABLES
	const route = useRoute()
	const router = useRouter()
	const notification = useNotificationStore()

	// DEVICE DATA FETCHING
	const { data: device } = await useApi<DeviceWithGroup>(
		`/devices/${route.params.id}`
	)

	// CONTROLS
	const query = reactive({
		fields: [],
		start: '-1h',
		stop: '0h',
		interval: '1m',
		aggregation: 'mean',
	})
	watch(() => query.fields, fetchMetrics)

	function isFieldSelected(field: string) {
		return !!query.fields.find((x) => x === field)
	}

	enum TimeFrame {
		LAST_5_MINUTES = '5m',
		LAST_HOUR = '1h',
		LAST_DAY = '24h',
		LAST_WEEK = '7d',
		LAST_MONTH = '30d',
	}

	const timeFrame = ref(TimeFrame.LAST_HOUR)
	watch(timeFrame, () => {
		switch (timeFrame.value) {
			case TimeFrame.LAST_5_MINUTES:
				query.start = '-5m'
				query.stop = '0m'
				query.interval = '10s'
				break
			case TimeFrame.LAST_HOUR:
				query.start = '-1h'
				query.stop = '0h'
				query.interval = '1m'
				break
			case TimeFrame.LAST_DAY:
				query.start = '-24h'
				query.stop = '0h'
				query.interval = '20m'
				break
			case TimeFrame.LAST_WEEK:
				query.start = '-7d'
				query.stop = '0d'
				query.interval = '1h'
				break
			case TimeFrame.LAST_MONTH:
				query.start = '-30d'
				query.stop = '0d'
				query.interval = '6h'
				break
		}
		fetchMetrics()
	})

	// METRIC FETCHING
	const isFetchingMetrics = ref(false)
	const series = ref([])
	const availableFields = ref([] as string[])

	async function fetchMetrics() {
		if (isFetchingMetrics.value) return
		isFetchingMetrics.value = true

		try {
			availableFields.value = await $api<string[]>(
				`/devices/${route.params.id}/metrics-fields?start=${query.start}&stop=${query.stop}`
			)

			// There are no metrics for the given time range.
			if (availableFields.value.length === 0) {
				query.fields = []
				isFetchingMetrics.value = false
				return
			}

			// Remove fields that no longer exists
			query.fields = query.fields.filter(
				(selectedField) =>
					!!availableFields.value.find((field) => field === selectedField)
			)

			// Select the first metric if none is selected.
			if (query.fields.length === 0) {
				query.fields.push(availableFields.value[0])
			}

			const metrics = await $api<MultiMetric[]>(
				`/devices/${route.params.id}/metrics?field=${query.fields.join(
					','
				)}&start=${query.start}&stop=${query.stop}&interval=${
					query.interval
				}&aggregation=${query.aggregation}`
			)

			series.value = query.fields.map((field, index) => ({
				name: field,
				data: metrics
					.filter((x) => x.values[index] !== null)
					.map((x) => [x.time, x.values[index]]),
			}))
		} catch (err) {
			notification.internalServerError()
			console.error(err)
		} finally {
			isFetchingMetrics.value = false
		}
	}

	onMounted(fetchMetrics)

	// APEXCHARTS
	function formatter(val) {
		return val.toFixed(2)
	}

	const chartOptions = ref<ApexOptions>({
		chart: {
			redrawOnWindowResize: true,
			type: 'area',
			stacked: false,
			zoom: {
				type: 'x',
				enabled: false,
				autoScaleYaxis: true,
			},
			animations: {
				enabled: false,
			},
			toolbar: {
				autoSelected: 'zoom',
			},
		},
		dataLabels: {
			enabled: false,
		},
		markers: {
			size: 0,
		},
		yaxis: {
			opposite: true,
			labels: {
				formatter: formatter,
			},
			title: {
				text: 'Values (units unknown)',
			},
		},
		xaxis: {
			type: 'datetime',
			title: {
				text: 'Time',
			},
		},
		tooltip: {
			shared: true,
			y: {
				formatter: formatter,
			},
		},
	})

	// REFRESH INTERVAL LOGIC
	enum RefreshInterval {
		NONE = '-',
		EVERY_10_SECONDS = '10s',
		EVERY_MINUTE = '1m',
		EVERY_5_MINUTES = '5m',
		EVERY_10_MINUTES = '10m',
	}

	const interval = ref<ReturnType<typeof setInterval> | null>(null)
	const refreshInterval = ref(RefreshInterval.NONE)

	watch(refreshInterval, () => {
		if (interval.value) {
			clearInterval(interval.value)
			interval.value = null
		}

		let ms
		switch (refreshInterval.value) {
			case RefreshInterval.EVERY_10_SECONDS:
				ms = 10 * 1000
				break
			case RefreshInterval.EVERY_MINUTE:
				ms = 60 * 1000
				break
			case RefreshInterval.EVERY_5_MINUTES:
				ms = 5 * 60 * 1000
				break
			case RefreshInterval.EVERY_10_MINUTES:
				ms = 10 * 60 * 1000
			default:
				return
		}

		interval.value = setInterval(fetchMetrics, ms)
	})

	onUnmounted(() => {
		if (interval.value) {
			clearInterval(interval.value)
			interval.value = null
		}
	})

	// EDIT AND DELETE MODALS
	const editModalOpen = ref(false)
	const deleteModalOpen = ref(false)
	function onEdit(updatedDevice: DeviceWithGroup) {
		device.value = updatedDevice
		notification.show(
			NotificationType.SUCCESS,
			'Successfully updated',
			`Device ${device.value.name} has been successfully updated`,
			10
		)
	}

	function onDelete() {
		router.push('/')
		notification.show(
			NotificationType.SUCCESS,
			'Successfully deleted',
			`Device ${device.value.name} has been successfully deleted`,
			10
		)
	}

	/*
	const positionmode = ref('History')
	const metricInput = ref(metric_query.value.fields[0])
	const main_map: Ref<LMap> = ref()

	const zoom = 16
	const path = ref([[0, 0]])
	const currentPosition = ref([0, 0])

	function parseMapValues() {
		if (posMetric.value != null) {
			path.value = posMetric.value.map((x) => x.values)
		} else {
			path.value = [[0, 0]]
		}
	}

	function switchPositionMode() {
		if (positionmode.value == 'History') {
			positionmode.value = 'Current Position'
			return
		}
		if (positionmode.value == 'Current Position') {
			positionmode.value = 'History'
			return
		}
	}

	function updateCurrentPosition(): number[] | null {
		if (posMetric.value == null || posMetric.value.length < 1) {
			return [0, 0]
		}
		currentPosition.value = posMetric.value[posMetric.value.length - 1].values
		// TODO: UpdatePosition here
	}
	updateCurrentPosition()
	*/
</script>
