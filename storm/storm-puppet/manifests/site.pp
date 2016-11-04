node 'storm.nimbus' {
  $cluster = 'storm1'
  include storm::nimbus
  include storm::ui
}

node /storm.supervisor[1-9]/ {
  $cluster = 'storm1'
  include storm::supervisor
}

node /storm.zookeeper[1-9]/ {
  include storm::zoo
}

