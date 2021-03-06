package io.kotless.gen.factory.apigateway

import io.kotless.Webapp
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.gen.factory.route.dynamic.DynamicRouteFactory
import io.kotless.gen.factory.route.static.StaticRouteFactory
import io.kotless.hcl.HCLEntity
import io.kotless.hcl.ref
import io.kotless.terraform.functions.eval
import io.kotless.terraform.functions.timestamp
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_deployment

object DeploymentFactory : GenerationFactory<Webapp.ApiGateway.Deployment, DeploymentFactory.Output> {
    data class Output(val stage_name: String)

    override fun mayRun(entity: Webapp.ApiGateway.Deployment, context: GenerationContext) = context.output.check(context.webapp.api, RestAPIFactory)
        && context.webapp.api.dynamics.all { context.output.check(it, DynamicRouteFactory) }
        && context.webapp.api.statics.all { context.output.check(it, StaticRouteFactory) }

    override fun generate(entity: Webapp.ApiGateway.Deployment, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val api = context.output.get(context.webapp.api, RestAPIFactory)
        val statics = context.webapp.api.statics.map { context.output.get(it, StaticRouteFactory).integration }
        val dynamics = context.webapp.api.dynamics.map { context.output.get(it, DynamicRouteFactory).integration }

        val deployment = api_gateway_deployment(context.names.tf(entity.name)) {
            depends_on = (statics + dynamics).toTypedArray()

            rest_api_id = api.rest_api_id
            stage_name = entity.version

            variables = object : HCLEntity() {
                val deployed_at by text(default = eval(timestamp()))
            }

            lifecycle {
                create_before_destroy = true
            }
        }

        return GenerationFactory.GenerationResult(Output(deployment::stage_name.ref), deployment)
    }
}
