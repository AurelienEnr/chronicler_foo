# Chronicler foo

Project to test [fsanaulla/chronicler](https://github.com/fsanaulla/chronicler) 
an open source project connecting Scala to InfluxDB.

## Prerequisites

**Have an InfluxDB running without your own data!**

<span style="color:red"> **WARNING: "DROP SERIES FROM /.*/" is ran on "test_db"**. </span>

Read code and use at your own risks. 

### Docker

```bash
# move to this project dir
cd . # replace with correct path if needed

# create a directory in which InfluxDB will save data
mkdir -p data/influxdb

# full path of data dir
influxdb_data_dir=$(pwd)/data/influxdb

# starting an influxdb container with data being in 'data/influxdb' (note: docker needs full path)
sudo docker run -p 8086:8086 -v $influxdb_data_dir:/var/lib/influxdb influxdb
```
