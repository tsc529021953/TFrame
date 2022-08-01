package com.sc.lib_weather.bean;

/**
 * @author sc
 * @date 2021/9/26 14:46
 */
public class XiaoMiWeaherBaen {

    public CurrentInfo current;

    public class CurrentInfo{
        public Info1 feelsLike;
        public Info1 humidity;
        public Info1 pressure;
        public String pubTime;
        public Info1 temperature;
        public String uvIndex;
        public Info1 visibility;
        public String weather;
        public Wind wind;
    }

    public class Wind{
        public Info1 direction;
        public Info1 speed;
    }

    public class Info1{
        public String unit;
        public String value;
    }


}

/*{"weatherinfo":{"SD":"79%","WD":"东北偏北风","WS":"2级","WSE":"","city":"","cityid":"101010100","isRadar":"1","radar":"JC_RADAR_AZ9010_JB","temp":"22","time":"14:16","weather":"多云"}}*/

/*{
	"current": {
		"feelsLike": {
			"unit": "℃",
			"value": "29"
		},
		"humidity": {
			"unit": "%",
			"value": "48"
		},
		"pressure": {
			"unit": "hPa",
			"value": "1014"
		},
		"pubTime": "2021-09-26T14:30:00+08:00",
		"temperature": {
			"unit": "℃",
			"value": "30"
		},
		"uvIndex": "5",
		"visibility": {
			"unit": "km",
			"value": ""
		},
		"weather": "1",
		"wind": {
			"direction": {
				"unit": "°",
				"value": "99.0"
			},
			"speed": {
				"unit": "km/h",
				"value": "6.0"
			}
		}
	},
	"forecastDaily": {
		"aqi": {
			"brandInfo": {
				"brands": [{
					"brandId": "caiyun",
					"logo": "http://f5.market.mi-img.com/download/MiSafe/07fa34263d698a7a9a8050dde6a7c63f8f243dbf3/a.webp",
					"names": {
						"zh_TW": "彩雲天氣",
						"en_US": "彩云天气",
						"zh_CN": "彩云天气"
					},
					"url": ""
				}]
			},
			"pubTime": "2021-09-26T00:00:00+08:00",
			"status": 0,
			"value": [42, 32, 34, 40, 47, 40, 37, 29, 22, 27, 33, 29, 26, 18, 43]
		},
		"moonPhase": {
			"moonPhaseList": [29, 36, 43, 50, 57, 64, 71, 78, 85, 92, -100, -93, -86, -79, -72],
			"pubTime": "2021-09-26 11:10:03"
		},
		"precipitationProbability": {
			"status": 0,
			"value": ["1", "5", "5", "55", "0"]
		},
		"pubTime": "2021-09-26T13:00:00+08:00",
		"status": 0,
		"sunRiseSet": {
			"status": 0,
			"value": [{
				"from": "2021-09-26T05:43:00+08:00",
				"to": "2021-09-26T17:47:00+08:00"
			}, {
				"from": "2021-09-27T05:44:00+08:00",
				"to": "2021-09-27T17:45:00+08:00"
			}, {
				"from": "2021-09-28T05:44:00+08:00",
				"to": "2021-09-28T17:44:00+08:00"
			}, {
				"from": "2021-09-29T05:45:00+08:00",
				"to": "2021-09-29T17:43:00+08:00"
			}, {
				"from": "2021-09-30T05:45:00+08:00",
				"to": "2021-09-30T17:42:00+08:00"
			}, {
				"from": "2021-10-01T05:46:00+08:00",
				"to": "2021-10-01T17:40:00+08:00"
			}, {
				"from": "2021-10-02T05:47:00+08:00",
				"to": "2021-10-02T17:39:00+08:00"
			}, {
				"from": "2021-10-03T05:47:00+08:00",
				"to": "2021-10-03T17:38:00+08:00"
			}, {
				"from": "2021-10-04T05:48:00+08:00",
				"to": "2021-10-04T17:37:00+08:00"
			}, {
				"from": "2021-10-05T05:48:00+08:00",
				"to": "2021-10-05T17:36:00+08:00"
			}, {
				"from": "2021-10-06T05:49:00+08:00",
				"to": "2021-10-06T17:34:00+08:00"
			}, {
				"from": "2021-10-07T05:49:00+08:00",
				"to": "2021-10-07T17:33:00+08:00"
			}, {
				"from": "2021-10-08T05:50:00+08:00",
				"to": "2021-10-08T17:32:00+08:00"
			}, {
				"from": "2021-10-09T05:51:00+08:00",
				"to": "2021-10-09T17:31:00+08:00"
			}, {
				"from": "2021-10-10T05:51:00+08:00",
				"to": "2021-10-10T17:30:00+08:00"
			}]
		},
		"temperature": {
			"status": 0,
			"unit": "℃",
			"value": [{
				"from": "30",
				"to": "21"
			}, {
				"from": "32",
				"to": "21"
			}, {
				"from": "31",
				"to": "22"
			}, {
				"from": "31",
				"to": "21"
			}, {
				"from": "28",
				"to": "17"
			}, {
				"from": "30",
				"to": "19"
			}, {
				"from": "32",
				"to": "22"
			}, {
				"from": "32",
				"to": "22"
			}, {
				"from": "32",
				"to": "23"
			}, {
				"from": "31",
				"to": "22"
			}, {
				"from": "26",
				"to": "22"
			}, {
				"from": "29",
				"to": "24"
			}, {
				"from": "30",
				"to": "26"
			}, {
				"from": "31",
				"to": "24"
			}, {
				"from": "32",
				"to": "23"
			}]
		},
		"weather": {
			"status": 0,
			"value": [{
				"from": "1",
				"to": "1"
			}, {
				"from": "1",
				"to": "0"
			}, {
				"from": "1",
				"to": "1"
			}, {
				"from": "7",
				"to": "7"
			}, {
				"from": "1",
				"to": "0"
			}, {
				"from": "0",
				"to": "0"
			}, {
				"from": "0",
				"to": "1"
			}, {
				"from": "0",
				"to": "0"
			}, {
				"from": "0",
				"to": "0"
			}, {
				"from": "0",
				"to": "7"
			}, {
				"from": "2",
				"to": "7"
			}, {
				"from": "7",
				"to": "7"
			}, {
				"from": "7",
				"to": "7"
			}, {
				"from": "7",
				"to": "2"
			}, {
				"from": "2",
				"to": "2"
			}]
		},
		"wind": {
			"direction": {
				"status": 0,
				"unit": "°",
				"value": [{
					"from": "99.0",
					"to": "99.0"
				}, {
					"from": "141.17",
					"to": "106.83"
				}, {
					"from": "138.37",
					"to": "114.3"
				}, {
					"from": "316.15",
					"to": "334.37"
				}, {
					"from": "353.51",
					"to": "1.97"
				}, {
					"from": "128.4",
					"to": "99.78"
				}, {
					"from": "143.47",
					"to": "63.0"
				}, {
					"from": "128.91",
					"to": "111.3"
				}, {
					"from": "151.01",
					"to": "149.92"
				}, {
					"from": "160.3",
					"to": "162.11"
				}, {
					"from": "125.11",
					"to": "43.1"
				}, {
					"from": "35.82",
					"to": "38.52"
				}, {
					"from": "37.35",
					"to": "39.7"
				}, {
					"from": "12.72",
					"to": "352.31"
				}, {
					"from": "5.78",
					"to": "17.37"
				}]
			},
			"speed": {
				"status": 0,
				"unit": "km/h",
				"value": [{
					"from": "6.0",
					"to": "6.0"
				}, {
					"from": "6.37",
					"to": "10.05"
				}, {
					"from": "6.13",
					"to": "10.78"
				}, {
					"from": "10.44",
					"to": "18.74"
				}, {
					"from": "9.35",
					"to": "15.69"
				}, {
					"from": "4.98",
					"to": "9.1"
				}, {
					"from": "5.48",
					"to": "8.51"
				}, {
					"from": "9.02",
					"to": "17.91"
				}, {
					"from": "12.17",
					"to": "18.5"
				}, {
					"from": "11.76",
					"to": "16.79"
				}, {
					"from": "5.83",
					"to": "6.5"
				}, {
					"from": "9.96",
					"to": "12.23"
				}, {
					"from": "11.69",
					"to": "15.63"
				}, {
					"from": "18.92",
					"to": "22.06"
				}, {
					"from": "24.14",
					"to": "26.4"
				}]
			}
		}
	},
	"forecastHourly": {
		"aqi": {
			"brandInfo": {
				"brands": [{
					"brandId": "caiyun",
					"logo": "http://f5.market.mi-img.com/download/MiSafe/07fa34263d698a7a9a8050dde6a7c63f8f243dbf3/a.webp",
					"names": {
						"zh_TW": "彩雲天氣",
						"en_US": "彩云天气",
						"zh_CN": "彩云天气"
					},
					"url": ""
				}]
			},
			"pubTime": "2021-09-26T15:00:00+08:00",
			"status": 0,
			"value": [21, 21, 21, 23, 23, 24, 26, 26, 27, 27, 29, 29, 29, 29, 29, 30, 30, 31, 33, 33, 34, 34, 34]
		},
		"desc": "逐小时预报",
		"status": 0,
		"temperature": {
			"pubTime": "2021-09-26T15:00:00+08:00",
			"status": 0,
			"unit": "",
			"value": [30, 29, 29, 27, 26, 25, 24, 24, 23, 23, 23, 22, 22, 22, 21, 23, 24, 25, 27, 28, 30, 31, 31]
		},
		"weather": {
			"pubTime": "2021-09-26T15:00:00+08:00",
			"status": 0,
			"value": [1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
		},
		"wind": {
			"status": 0,
			"value": [{
				"datetime": "2021-09-26T15:00:00.000+08:00",
				"direction": "115.64",
				"speed": "12.4"
			}, {
				"datetime": "2021-09-26T16:00:00.000+08:00",
				"direction": "118.13",
				"speed": "12.09"
			}, {
				"datetime": "2021-09-26T17:00:00.000+08:00",
				"direction": "111.32",
				"speed": "9.73"
			}, {
				"datetime": "2021-09-26T18:00:00.000+08:00",
				"direction": "119.73",
				"speed": "8.84"
			}, {
				"datetime": "2021-09-26T19:00:00.000+08:00",
				"direction": "131.27",
				"speed": "8.32"
			}, {
				"datetime": "2021-09-26T20:00:00.000+08:00",
				"direction": "147.47",
				"speed": "6.84"
			}, {
				"datetime": "2021-09-26T21:00:00.000+08:00",
				"direction": "159.03",
				"speed": "6.8"
			}, {
				"datetime": "2021-09-26T22:00:00.000+08:00",
				"direction": "170.07",
				"speed": "6.18"
			}, {
				"datetime": "2021-09-26T23:00:00.000+08:00",
				"direction": "170.28",
				"speed": "6.07"
			}, {
				"datetime": "2021-09-27T00:00:00.000+08:00",
				"direction": "178.85",
				"speed": "6.32"
			}, {
				"datetime": "2021-09-27T01:00:00.000+08:00",
				"direction": "187.05",
				"speed": "6.17"
			}, {
				"datetime": "2021-09-27T02:00:00.000+08:00",
				"direction": "193.31",
				"speed": "5.95"
			}, {
				"datetime": "2021-09-27T03:00:00.000+08:00",
				"direction": "201.24",
				"speed": "5.6"
			}, {
				"datetime": "2021-09-27T04:00:00.000+08:00",
				"direction": "203.51",
				"speed": "4.93"
			}, {
				"datetime": "2021-09-27T05:00:00.000+08:00",
				"direction": "208.71",
				"speed": "4.2"
			}, {
				"datetime": "2021-09-27T06:00:00.000+08:00",
				"direction": "207.86",
				"speed": "4.28"
			}, {
				"datetime": "2021-09-27T07:00:00.000+08:00",
				"direction": "200.61",
				"speed": "3.2"
			}, {
				"datetime": "2021-09-27T08:00:00.000+08:00",
				"direction": "168.98",
				"speed": "4.11"
			}, {
				"datetime": "2021-09-27T09:00:00.000+08:00",
				"direction": "155.57",
				"speed": "5.56"
			}, {
				"datetime": "2021-09-27T10:00:00.000+08:00",
				"direction": "137.95",
				"speed": "5.27"
			}, {
				"datetime": "2021-09-27T11:00:00.000+08:00",
				"direction": "127.86",
				"speed": "4.83"
			}, {
				"datetime": "2021-09-27T12:00:00.000+08:00",
				"direction": "114.02",
				"speed": "5.68"
			}, {
				"datetime": "2021-09-27T13:00:00.000+08:00",
				"direction": "103.67",
				"speed": "7.67"
			}]
		}
	},
	"indices": {
		"indices": [{
			"type": "uvIndex",
			"value": "6"
		}, {
			"type": "humidity",
			"value": "48"
		}, {
			"type": "feelsLike",
			"value": "29"
		}, {
			"type": "pressure",
			"value": "1014"
		}, {
			"type": "carWash",
			"value": "0"
		}, {
			"type": "sports",
			"value": "0"
		}],
		"pubTime": "",
		"status": 0
	},
	"alerts": [],
	"yesterday": {
		"aqi": "",
		"date": "2021-09-25T12:00:00+08:00",
		"status": 0,
		"sunRise": "2021-09-25T05:43:00+08:00",
		"sunSet": "2021-09-25T17:48:00+08:00",
		"tempMax": "30",
		"tempMin": "19",
		"weatherEnd": "1",
		"weatherStart": "1",
		"windDircEnd": "360",
		"windDircStart": "90",
		"windSpeedEnd": "0.0",
		"windSpeedStart": "29.0"
	},
	"url": {
		"weathercn": "",
		"caiyun": ""
	},
	"brandInfo": {
		"brands": [{
			"brandId": "caiyun",
			"logo": "http://f5.market.mi-img.com/download/MiSafe/069835733640846b1b2613a855328d2b6df404343/a.webp",
			"names": {
				"zh_TW": "彩雲天氣",
				"en_US": "彩云天气",
				"zh_CN": "彩云天气"
			},
			"url": ""
		}, {
			"brandId": "weatherbj",
			"logo": "",
			"names": {
				"zh_TW": "北京气象局",
				"en_US": "北京气象局",
				"zh_CN": "北京气象局"
			},
			"url": ""
		}]
	},
	"preHour": [{
		"feelsLike": {
			"unit": "℃",
			"value": "29"
		},
		"humidity": {
			"unit": "%",
			"value": "48"
		},
		"pressure": {
			"unit": "hPa",
			"value": "1015"
		},
		"pubTime": "2021-09-26T14:00:00+08:00",
		"temperature": {
			"unit": "℃",
			"value": "30"
		},
		"uvIndex": "7",
		"visibility": {
			"unit": "km",
			"value": ""
		},
		"weather": "1",
		"wind": {
			"direction": {
				"unit": "°",
				"value": "89.0"
			},
			"speed": {
				"unit": "km/h",
				"value": "9.0"
			}
		},
		"aqi": {
			"aqi": "42",
			"brandInfo": {
				"brands": [{
					"brandId": "CNEMC",
					"logo": "",
					"names": {
						"zh_TW": "中國環境監測總站",
						"en_US": "CNEMC",
						"zh_CN": "中国环境监测总站"
					},
					"url": ""
				}]
			},
			"co": "0.3",
			"no2": "14",
			"o3": "134",
			"pm10": "27",
			"pm25": "15",
			"primary": "",
			"pubTime": "2021-09-26T13:00:00+08:00",
			"so2": "9",
			"src": "中国环境监测总站",
			"status": 0,
			"suggest": "空气很好，快呼吸新鲜空气，拥抱大自然吧",
			"pm25Desc": "PM2.5易携带重金属、微生物等有害物质，对人体健康影响较大",
			"pm10Desc": "PM10对人的影响要大于其他任何污染物，长期暴露于污染环境可能导致罹患心血管和呼吸道疾病甚至肺癌",
			"no2Desc": "短期浓度超过200微克/立方米时，二氧化氮是一种引起呼吸道严重发炎的有毒气体",
			"so2Desc": "人为的二氧化硫主要来源为家庭取暖，发电和机动车而燃烧含有硫磺的矿物燃料，以及对含有硫磺的矿物的冶炼",
			"coDesc": "一氧化碳八成来自汽车尾气，交通高峰期时，公路沿线产生的CO浓度会高于平常",
			"o3Desc": "臭氧俗称晴空杀手，无色无味，但对人体的伤害不比PM2.5低，浓度高时建议减少夏季午后的外出活动，如果不开窗效果更佳"
		}
	}],
	"sourceMaps": {
		"current": {
			"feelsLike": "weatherbj(locationKey=101210411)",
			"weather": "weatherbj(locationKey=101210411)",
			"temperature": "weatherbj(locationKey=101210411)",
			"humidity": "weatherbj(locationKey=101210411)",
			"pressure": "weatherbj(locationKey=101210411)",
			"windDir": "caiyun(locationKey=101210411)",
			"windSpeed": "caiyun(locationKey=101210411)",
			"uvIndex": "accucn(locationKey=2333647,locale=zh_CN)"
		},
		"indices": {
			"feelsLikeV1": "weatherbj(locationKey=101210411)",
			"pressureV1": "weatherbj(locationKey=101210411)",
			"uvIndexV1": "weatherbj(locationKey=101210411)",
			"sportsV1": "weatherbj(locationKey=101210411)",
			"carWashV1": "weatherbj(locationKey=101210411)"
		},
		"daily": {
			"preciProbability": "accucn(locationKey=2333647,locale=zh_CN)",
			"weather": "weatherbj(locationKey=101210411)",
			"temperature": "weatherbj(locationKey=101210411)",
			"sunRiseSet": "weatherbj(locationKey=101210411)",
			"aqi": "caiyun(locationKey=29.816,121.547,locale=zh_CN)",
			"wind": "caiyun(locationKey=101210411)"
		},
		"clientInfo": {
			"appVersion": 12070100,
			"isLocated": true,
			"isGlobal": false,
			"appKey": "weather20151024",
			"locale": "zh_CN"
		},
		"hourly": {
			"weather": "weatherbj(locationKey=101210411)",
			"temperature": "weatherbj(locationKey=101210411)",
			"aqi": "caiyun(locationKey=29.816,121.547,locale=zh_CN)",
			"wind": "caiyun(locationKey=29.816,121.547, latitude=null, longitude=null)",
			"desc": "caiyun(locationKey=29.816,121.547, latitude=null, longitude=null)"
		}
	},
	"updateTime": 1632638092682,
	"aqi": {
		"aqi": "42",
		"brandInfo": {
			"brands": [{
				"brandId": "CNEMC",
				"logo": "",
				"names": {
					"zh_TW": "中國環境監測總站",
					"en_US": "CNEMC",
					"zh_CN": "中国环境监测总站"
				},
				"url": ""
			}]
		},
		"co": "0.3",
		"no2": "14",
		"o3": "134",
		"pm10": "27",
		"pm25": "15",
		"primary": "",
		"pubTime": "2021-09-26T14:10:07+08:00",
		"so2": "9",
		"src": "中国环境监测总站",
		"status": 0,
		"suggest": "空气很好，快呼吸新鲜空气，拥抱大自然吧",
		"pm25Desc": "PM2.5的主要来源是燃料、木材和其他生物质燃料的燃烧",
		"pm10Desc": "PM10对人的影响要大于其他任何污染物，长期暴露于污染环境可能导致罹患心血管和呼吸道疾病甚至肺癌",
		"no2Desc": "二氧化氮有刺激性特殊臭味，但浓度低时人体不会感知到",
		"so2Desc": "人为的二氧化硫主要来源为家庭取暖，发电和机动车而燃烧含有硫磺的矿物燃料，以及对含有硫磺的矿物的冶炼",
		"coDesc": "一氧化碳是无色，无臭，无味气体，但吸入对人体有十分大的危害",
		"o3Desc": "地面的臭氧主要由车辆和工业释放出的氧化氮等污染物以及由机动车、溶剂和工业释放的挥发性有机化合物与阳光反应而生成"
	},
	"chs": [{
		"type": "CWA6"
	}],
	"minutely": {
		"status": 0,
		"new": "new",
		"probability": {
			"maxProbability": "0%",
			"probabilityDesc": "2小时降水概率",
			"probabilityDescV2": "2小时内无降水"
		},
		"precipitation": {
			"description": "未来两小时不会下雨，放心出门吧",
			"firstRainOrSnow": false,
			"headDescription": "2小时降水概率",
			"headIconType": "rain_0",
			"isModify": false,
			"isRainOrSnow": 2,
			"isShow": false,
			"modifyInHour": false,
			"probability": [0, 0, 0, 0],
			"pubTime": "2021-09-26T14:30:02+08:00",
			"shortDescription": "",
			"status": 0,
			"value": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
			"weather": "1"
		}
	}
}*/
//https://www.sojson.com/
