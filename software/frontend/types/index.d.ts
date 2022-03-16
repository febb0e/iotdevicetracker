type UserRole = 'USER' | 'SYSADMIN'

interface User {
	id: number
	username: string
	email: string
	verified: booelan
	role: UserRole
	mfaEnabled: boolean
	updatedAt: string
	createdAt: string
}

type DeviceGroupRole = 'OBSERVER' | 'ADMIN'

interface DeviceGroup {
	id: number
	name: string
	role: DeviceGroupRole
	updatedAt: string
	createdAt: string
	amountOfDevices: number
}

interface DeviceGroupUser {
	id: {
		userId: number
		deviceGroupId: number
	}
	role: DeviceGroupRole
	user: User
	updateDAt: string
	createdAt: string
}

interface Device {
	id: number
	identifier: string
	name: string
	updatedAt: string
	createdAt: string
}

interface DeviceWithGroup extends Device {
	role: DeviceGroupRole
}

interface DeviceWithToken extends DeviceWithGroup {
	token: string
}

interface MetricQuery {
	fields: string[]
	start: string
	stop?: string
	interval?: string
	aggregation?: string
}

interface MultiMetric {
	time: string
	identifier: string
	name: string
	values: number[]
}
