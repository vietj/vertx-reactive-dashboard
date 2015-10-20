$vertx.deploy_verticle 'maven:io.vertx:processmon:1.0-SNAPSHOT'

$vertx.event_bus.local_consumer 'processmon' do |msg|
  process = msg.body
  metrics = {
      process['pid'] => {
          :CPU => process['cpu'],
          :Mem => process['mem'],
      }
  }
  $vertx.event_bus.publish 'metrics', metrics
end
