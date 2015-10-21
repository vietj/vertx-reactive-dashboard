$vertx.deploy_verticle 'maven:io.vertx:collector:1.0-SNAPSHOT'

$vertx.event_bus.local_consumer 'metrics' do |msg|
  metrics = msg.body
  puts "#{metrics}"
end
